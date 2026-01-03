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

import java.util.Map;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class AuthIntegrationTest {
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private JdbcTemplate jdbcTemplate;

    @BeforeEach
    void cleanDb() {
        jdbcTemplate.execute("DELETE FROM tasks");
        jdbcTemplate.execute("DELETE FROM teams_members");
        jdbcTemplate.execute("DELETE FROM teams");
        jdbcTemplate.execute("DELETE FROM users_roles");
        jdbcTemplate.execute("DELETE FROM users");
        jdbcTemplate.execute("DELETE FROM roles");
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
                .andExpect(jsonPath("$.username").value("intuser"));
    }
}
