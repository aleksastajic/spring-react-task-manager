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
            // Use healthcheck wait strategy: executes actual query to verify DB is ready.
            // This is more reliable than log messages for avoiding Hikari validation warnings.
            .waitingFor(Wait.forHealthcheck()
                .withStartupTimeout(Duration.ofSeconds(120)));

    @DynamicPropertySource
    static void overrideSpringProperties(DynamicPropertyRegistry registry) {
        // Give Postgres extra time to finish internal warmup after container reports ready.
        // This prevents Hikari from attempting connections before Postgres is truly ready.
        try {
            Thread.sleep(3000);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        
        registry.add("spring.datasource.url", POSTGRES::getJdbcUrl);
        registry.add("spring.datasource.username", POSTGRES::getUsername);
        registry.add("spring.datasource.password", POSTGRES::getPassword);

        // Reduce flakiness during container startup on CI runners.
        // Relax fail-fast to allow the container/DB to finish warming up on slow CI runners.
        registry.add("spring.datasource.hikari.initializationFailTimeout", () -> "-1");
        registry.add("spring.datasource.hikari.connectionTimeout", () -> "30000");
        // Give more time for validation to avoid false-positive closed connection warnings.
        registry.add("spring.datasource.hikari.validationTimeout", () -> "10000");
        registry.add("spring.datasource.hikari.maximumPoolSize", () -> "5");
        registry.add("spring.datasource.hikari.minimumIdle", () -> "0");
        registry.add("spring.datasource.hikari.idleTimeout", () -> "60000");
        // Increase maxLifetime to avoid premature connection recycling during slow container startup.
        registry.add("spring.datasource.hikari.maxLifetime", () -> "120000");
        registry.add("spring.datasource.hikari.keepaliveTime", () -> "0");

        // Ensure Flyway runs against the same container DB.
        registry.add("spring.flyway.url", POSTGRES::getJdbcUrl);
        registry.add("spring.flyway.user", POSTGRES::getUsername);
        registry.add("spring.flyway.password", POSTGRES::getPassword);
    }
}
