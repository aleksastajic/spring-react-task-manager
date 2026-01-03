
# Project Structure

## Backend (Spring Boot)

**src/main/java/com/taskmanager/api/**

- **TaskManagerApiApplication.java** — Main Spring Boot application entry point.
- **config/** — General configuration classes (e.g., CORS, beans).
- **controller/** — REST controllers for API endpoints:
	- **AuthController**: Auth/register/login
	- **UserController**: User profile
	- **TeamController**: Team CRUD, member management
	- **TaskController**: Task CRUD, assign/unassign, status change, list by team/user
- **dto/** — Data Transfer Objects for requests and responses (e.g., TaskDto, TaskCreateDto, TeamDto, UserDto).
- **entity/** — JPA entities (User, Role, Team, Task, Priority, Status, etc.).
- **exception/** — Global exception handler and custom exceptions.
- **mapper/** — Mappers for converting between entities and DTOs.
- **repository/** — Spring Data JPA repositories for database access.
- **security/** — Security config, JWT filter, user details, etc.
- **service/** — Service interfaces and business logic:
	- **TeamService/Impl**: Team CRUD, admin/member logic
	- **TaskService/Impl**: Task CRUD, assign/unassign, status, permission checks

**src/main/resources/**
- **application.properties** — Main backend configuration (DB, JWT, logging).

**src/test/java/com/taskmanager/api/**
- Integration and unit tests for services and controllers.

---

See README.md for API usage and Swagger documentation details.
