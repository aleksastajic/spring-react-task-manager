# Project Structure

This document describes the high-level layout of the repository.

## Top-Level

- `backend/` — Spring Boot API
- `frontend/` — React + Vite client
- `README.md` — Setup, scripts, and project overview

## Backend (Spring Boot)

**Entry point**

- `backend/src/main/java/com/taskmanager/api/TaskManagerApiApplication.java`

**Packages (`backend/src/main/java/com/taskmanager/api/`)**

- `config/` — Application configuration (CORS, OpenAPI, dev seeder)
- `controller/` — REST controllers
  - `AuthController` — register/login
  - `UserController` — current user profile endpoints
  - `TeamController` — teams + membership
  - `TaskController` — tasks + assignment/status
- `dto/` — Request/response DTOs
- `entity/` — JPA entities (User, Role, Team, Task, enums)
- `exception/` — API error model + global exception handling
- `mapper/` — Entity/DTO mappers
- `repository/` — Spring Data JPA repositories
- `security/` — Spring Security config + JWT utilities/filter
- `service/` — Service interfaces
- `service/impl/` — Service implementations with permission checks

**Resources (`backend/src/main/resources/`)**

- `application.properties` — Default config (uses env var overrides)
- `application-dev.properties` — Dev-only overrides (verbose logging)
- `db/migration/active/` — Flyway migrations used at runtime

**Tests (`backend/src/test/java/`)**

- Integration tests (controller/API flows)
- Unit tests (service layer)

**Utility scripts (`backend/scripts/`)**

- `run-build-log.sh` — Build/package with timestamped logs in `backend/logs/`
- `run-tests-log.sh` — Run tests with timestamped logs in `backend/logs/`

## Frontend (React + Vite)

**Entry points**

- `frontend/src/main.jsx` — React bootstrap
- `frontend/src/App.jsx` — Routes + app shell

**Source (`frontend/src/`)**

- `api/` — Central API client (`fetch` wrappers, JWT handling)
- `components/` — Reusable UI components (Button, Card, ErrorBoundary, icons)
- `context/` — React context providers (AuthContext)
- `hooks/` — Custom hooks (useAuth)
- `pages/` — Route-level pages (Dashboard, Login, Register, Profile, Teams, Tasks)
- `styles/` — Global styles (Tailwind entry + app CSS)

**Config**

- `frontend/vite.config.js` — Dev server + `/api` proxy to backend
- `frontend/tailwind.config.cjs` — Tailwind theme tokens
- `frontend/postcss.config.cjs` — PostCSS pipeline
