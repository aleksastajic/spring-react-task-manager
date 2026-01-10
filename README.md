# Task Manager (Spring Boot + React)

Full‑stack task manager with JWT authentication, teams, and tasks.

## Tech Stack

**Backend**
- Java 17, Spring Boot
- Spring Security (JWT)
- Spring Data JPA (Hibernate)
- Flyway migrations
- PostgreSQL
- OpenAPI/Swagger via springdoc

**Frontend**
- React + Vite
- React Router
- Tailwind CSS

## Features

- Authentication: register/login, JWT‑protected API
- Users: view/update/delete current profile
- Teams: create/update/delete, manage membership, list teams by user
- Tasks: create/update/delete, assign/unassign, status changes, list by team/user
- Dev experience: Flyway migrations + dev-only seed data

## Run Locally

### Prerequisites

- Java 17+
- Node.js 18+ (or 20+)
- PostgreSQL (recommended: 16+)

### Backend

1. Create a database (default is `taskmanager_db`).
2. Configure the backend (defaults are fine for local dev):

Environment variables supported by the backend:

- `DB_URL` (default: `jdbc:postgresql://localhost:5432/taskmanager_db`)
- `DB_USERNAME` (default: `postgres`)
- `DB_PASSWORD` (default: `postgres`)
- `JWT_SECRET` (default: a dev-only value)
- `JWT_EXPIRATION` (default: `86400000` ms)

3. Run the API (dev profile enables seeded demo data):

```bash
cd backend
./mvnw spring-boot:run -Dspring-boot.run.profiles=dev
# or
SPRING_PROFILES_ACTIVE=dev ./mvnw spring-boot:run
```

API base URL: `http://localhost:8080`

Swagger UI:
- `http://localhost:8080/swagger-ui/index.html`

OpenAPI JSON:
- `http://localhost:8080/v3/api-docs`

### Frontend

```bash
cd frontend
npm install
npm run dev
```

App URL: `http://localhost:5173`

The Vite dev server proxies ` /api ` to `http://localhost:8080`.

## Database Migrations (Flyway)

- Flyway is enabled by default.
- Active migrations are in `backend/src/main/resources/db/migration/active/`.
- Hibernate auto-ddl is disabled (`spring.jpa.hibernate.ddl-auto=none`).

## Development Seed Data

When running with the `dev` profile, a dev-only seeder inserts demo data on an empty database:

- `backend/src/main/java/com/taskmanager/api/config/DevDataSeeder.java`

Seeded demo credentials:

- `alice` / `password` (ROLE_USER)
- `bob` / `password` (ROLE_ADMIN + ROLE_USER)

## Scripts

Backend helper scripts (write logs to `backend/logs/`):

```bash
cd backend
./scripts/run-build-log.sh
./scripts/run-tests-log.sh
```

Frontend scripts:

```bash
cd frontend
npm run dev
npm run build
npm run lint
```

## Project Layout

See [STRUCTURE.md](STRUCTURE.md) for a detailed overview of the backend and frontend folders.
