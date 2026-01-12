package com.taskmanager.api;

import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.containers.wait.strategy.Wait;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.time.Duration;

@Testcontainers(disabledWithoutDocker = true)
abstract class PostgresTestcontainerBase {
    @Container
    @SuppressWarnings("resource")
    static final PostgreSQLContainer<?> POSTGRES = new PostgreSQLContainer<>("postgres:16-alpine")
            .withDatabaseName("taskmanager_test")
            .withUsername("postgres")
            .withPassword("postgres")
            // Wait for 2nd occurrence of ready log. Postgres logs this twice: once during init,
            // once after final startup. The 2nd occurrence confirms the DB is truly ready.
            .waitingFor(Wait.forLogMessage(".*database system is ready to accept connections.*\\n", 2)
                .withStartupTimeout(Duration.ofSeconds(120)));

    @DynamicPropertySource
    static void overrideSpringProperties(DynamicPropertyRegistry registry) {
        // Ensure Postgres is truly ready by attempting actual connections and retrying.
        // This prevents Hikari from getting closed connections during Spring context init.
        ensurePostgresReady();
        
        registry.add("spring.datasource.url", POSTGRES::getJdbcUrl);
        registry.add("spring.datasource.username", POSTGRES::getUsername);
        registry.add("spring.datasource.password", POSTGRES::getPassword);

        // Reduce flakiness during container startup on CI runners.
        // Disable fail-fast and prevent eager connection initialization.
        registry.add("spring.datasource.hikari.initializationFailTimeout", () -> "-1");
        registry.add("spring.datasource.hikari.connectionTimeout", () -> "30000");
        registry.add("spring.datasource.hikari.validationTimeout", () -> "10000");
        registry.add("spring.datasource.hikari.maximumPoolSize", () -> "5");
        // Set to 0 to prevent Hikari from creating connections during pool startup
        registry.add("spring.datasource.hikari.minimumIdle", () -> "0");
        registry.add("spring.datasource.hikari.idleTimeout", () -> "60000");
        registry.add("spring.datasource.hikari.maxLifetime", () -> "120000");
        registry.add("spring.datasource.hikari.keepaliveTime", () -> "0");
        // Disable connection test query to avoid validation attempts during pool startup
        registry.add("spring.datasource.hikari.connectionTestQuery", () -> "");
        // Allow lazy initialization - connections created only when needed
        registry.add("spring.jpa.properties.hibernate.temp.use_jdbc_metadata_defaults", () -> "false");

        // Ensure Flyway runs against the same container DB.
        registry.add("spring.flyway.url", POSTGRES::getJdbcUrl);
        registry.add("spring.flyway.user", POSTGRES::getUsername);
        registry.add("spring.flyway.password", POSTGRES::getPassword);
    }

    private static void ensurePostgresReady() {
        String jdbcUrl = POSTGRES.getJdbcUrl();
        String username = POSTGRES.getUsername();
        String password = POSTGRES.getPassword();
        
        int maxAttempts = 10;
        for (int i = 0; i < maxAttempts; i++) {
            try (Connection conn = DriverManager.getConnection(jdbcUrl, username, password)) {
                // Successfully connected and closed - Postgres is ready
                return;
            } catch (SQLException e) {
                if (i < maxAttempts - 1) {
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException ie) {
                        Thread.currentThread().interrupt();
                        throw new RuntimeException("Interrupted while waiting for Postgres", ie);
                    }
                } else {
                    throw new RuntimeException("Postgres not ready after " + maxAttempts + " attempts", e);
                }
            }
        }
    }
}
