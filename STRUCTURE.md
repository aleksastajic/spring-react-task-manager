# Project Structure

## Backend (Spring Boot)

**src/main/java/com/taskmanager/api/**

- **TaskManagerApiApplication.java** — Main Spring Boot application entry point.
- **config/** — General configuration classes (e.g., CORS, beans).
- **controller/** — REST controllers for API endpoints (AuthController, UserController, TeamController, TaskController).
- **dto/** — Data Transfer Objects for requests and responses.
- **entity/** — JPA entities (User, Role, Team, Task, etc.).
- **exception/** — Global exception handler and custom exceptions.
- **mapper/** — Mappers for converting between entities and DTOs.
- **repository/** — Spring Data JPA repositories for database access.
- **security/** — Security config, JWT filter, user details, etc.
- **service/** — Service interfaces and business logic.

**src/main/resources/**
- **application.properties** — Main backend configuration (DB, JWT, logging).

**src/test/java/com/taskmanager/api/**
- Integration and unit tests for services and controllers.

---

See README.md for API usage and Swagger documentation details.
