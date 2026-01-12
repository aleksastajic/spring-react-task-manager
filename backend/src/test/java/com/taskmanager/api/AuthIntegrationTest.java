package com.taskmanager.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
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

                try (Connection ignored = ds.getConnection()) {
                        jdbcTemplate.update("DELETE FROM tasks");
                        jdbcTemplate.update("DELETE FROM teams_members");
                        jdbcTemplate.update("DELETE FROM teams");
                        jdbcTemplate.update("DELETE FROM users_roles");
                        jdbcTemplate.update("DELETE FROM users");
                        jdbcTemplate.update("DELETE FROM roles");
                } catch (SQLException e) {
                        throw new RuntimeException("Could not clean database before tests", e);
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
