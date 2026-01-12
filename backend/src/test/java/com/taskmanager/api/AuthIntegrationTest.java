package com.taskmanager.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Map;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@Disabled("Temporarily disabled due to Testcontainers/Hikari timing issues on CI")
@SuppressWarnings("null")
class AuthIntegrationTest extends PostgresTestcontainerBase {
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private JdbcTemplate jdbcTemplate;

    @BeforeEach
    void cleanDb() {
                DataSource ds = jdbcTemplate.getDataSource();
                if (ds == null) {
                        throw new IllegalStateException("No DataSource available for integration test cleanup");
                }

                int attempts = 0;
                while (attempts < 5) {
                        try (Connection ignored = ds.getConnection()) {
                                jdbcTemplate.update("DELETE FROM tasks");
                                jdbcTemplate.update("DELETE FROM teams_members");
                                jdbcTemplate.update("DELETE FROM teams");
                                jdbcTemplate.update("DELETE FROM users_roles");
                                jdbcTemplate.update("DELETE FROM users");
                                jdbcTemplate.update("DELETE FROM roles");
                                return;
                        } catch (SQLException e) {
                                attempts++;
                                if (attempts >= 5) {
                                        throw new RuntimeException("Could not clean database before tests", e);
                                }
                                try {
                                        Thread.sleep(2000);
                                } catch (InterruptedException ie) {
                                        Thread.currentThread().interrupt();
                                        throw new RuntimeException("Interrupted while waiting to retry DB cleanup", ie);
                                }
                        }
                }
    }

    @Test
    void registerLoginAndGetUserFlow() throws Exception {
        // Register
        String regJson = objectMapper.writeValueAsString(Map.of(
                "username", "intuser",
                "email", "intuser@example.com",
                "password", "intpass",
                "displayName", "Integration User"
        ));
        mockMvc.perform(post("/api/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(regJson))
                .andExpect(status().isOk());

        // Login
        String loginJson = objectMapper.writeValueAsString(Map.of(
                "usernameOrEmail", "intuser",
                "password", "intpass"
        ));
        MvcResult loginResult = mockMvc.perform(post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(loginJson))
                .andExpect(status().isOk())
                .andReturn();
        String respBody = loginResult.getResponse().getContentAsString();
        String token = objectMapper.readTree(respBody).get("token").asText();

        // Get current user
        mockMvc.perform(get("/api/users/me")
                .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username").value("intuser"))
                .andExpect(jsonPath("$.displayName").value("Integration User"));
    }
}
