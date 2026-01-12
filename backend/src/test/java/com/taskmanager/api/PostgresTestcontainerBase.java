package com.taskmanager.api;

import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.containers.wait.strategy.Wait;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.time.Duration;

@Testcontainers(disabledWithoutDocker = true)
abstract class PostgresTestcontainerBase {
    @Container
    @SuppressWarnings("resource")
    static final PostgreSQLContainer<?> POSTGRES = new PostgreSQLContainer<>("postgres:16-alpine")
            .withDatabaseName("taskmanager_test")
            .withUsername("postgres")
            .withPassword("postgres")
            // Wait for Postgres to be ready to accept connections (not just port-open).
            .waitingFor(Wait.forLogMessage(".*database system is ready to accept connections.*\\n", 1)
                .withStartupTimeout(Duration.ofSeconds(90)));

    @DynamicPropertySource
    static void overrideSpringProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", POSTGRES::getJdbcUrl);
        registry.add("spring.datasource.username", POSTGRES::getUsername);
        registry.add("spring.datasource.password", POSTGRES::getPassword);

        // Reduce flakiness during container startup on CI runners.
        registry.add("spring.datasource.hikari.initializationFailTimeout", () -> "60000");
        registry.add("spring.datasource.hikari.connectionTimeout", () -> "30000");
        registry.add("spring.datasource.hikari.validationTimeout", () -> "5000");
        registry.add("spring.datasource.hikari.maximumPoolSize", () -> "5");
        registry.add("spring.datasource.hikari.minimumIdle", () -> "0");
        registry.add("spring.datasource.hikari.idleTimeout", () -> "10000");
        registry.add("spring.datasource.hikari.maxLifetime", () -> "30000");
        registry.add("spring.datasource.hikari.keepaliveTime", () -> "15000");

        // Ensure Flyway runs against the same container DB.
        registry.add("spring.flyway.url", POSTGRES::getJdbcUrl);
        registry.add("spring.flyway.user", POSTGRES::getUsername);
        registry.add("spring.flyway.password", POSTGRES::getPassword);
    }
}
