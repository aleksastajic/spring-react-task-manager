# Task Manager (Spring Boot + React)

Full‑stack task manager with JWT authentication, teams, and tasks.

TL;DR
Full‑stack task manager (React + Vite frontend, Spring Boot backend) demonstrating JWT auth, teams, tasks, and end-to-end smoke tests.

## Highlights
- Full-stack: React/Vite frontend with Playwright smoke tests and a Spring Boot backend with Flyway migrations.
- Dev experience focus: Docker Compose for a one-command local demo, dev seeding, and test‑friendly configs.
- Authentication & APIs: JWT-based security plus OpenAPI for easy exploration.

## What I learned
- Integrating Playwright smoke tests into the dev lifecycle to validate main user flows.
- Configuring Vite proxy to develop frontend without changing backend URLs.
- Best practices for dev seeding so E2E tests and having stable demo credentials.

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

### Docker Compose (Recommended)

Starts Postgres + backend (Spring Boot) + frontend (Vite).

```bash
cp .env.example .env
docker compose up
```

To rebuild after code changes:

```bash
docker compose down
docker compose up --build
```

Common Docker lifecycle commands:

```bash
# Stop containers (keeps DB volume/data)
docker compose stop

# Start containers again
docker compose start

# Stop + remove containers (keeps volumes by default)
docker compose down

# Full reset (removes DB volume/data; dev seeder will repopulate)
docker compose down -v
```

Auto-start on reboot:
- By default, Docker Compose does not set a restart policy.
- If you want containers to automatically start when the Docker daemon starts, add `restart: unless-stopped` to each service in `docker-compose.yml`.
- On Linux, you also need Docker enabled at boot (e.g. `systemctl enable --now docker`).

Database port note:
- This repo’s Compose defaults the Postgres host port to `5433` to avoid conflicts with a locally-installed Postgres.
- You can override with `POSTGRES_PORT=5432` in `.env` if `5432` is free.

URLs:
- Frontend: `http://localhost:5173`
- Backend: `http://localhost:8080`

## Screenshots

<p align="center">
	<img src="frontend/public/assets/screenshots/dashboard.png" alt="Dashboard - Task manager" width="1000" />
</p>

## Quick Gallery

<p align="center">
	<img src="frontend/public/assets/screenshots/login.png" alt="Login" width="240" />
	<img src="frontend/public/assets/screenshots/dashboard.png" alt="Dashboard" width="240" />
	<img src="frontend/public/assets/screenshots/teams.png" alt="Teams" width="240" />
	<img src="frontend/public/assets/screenshots/tasks.png" alt="Tasks" width="240" />
</p>

_Captions:_ Login • Dashboard • Teams • Tasks

Notes:
- The backend uses Flyway migrations automatically.
- The `dev` profile seeds demo data and keeps demo credentials present even if you’re using a persistent DB volume.
- CORS allowlist is configurable via `CORS_ALLOWED_ORIGINS`.
- When running the frontend in Docker, Vite proxies `/api` to the backend using `VITE_PROXY_TARGET` (set in `docker-compose.yml`).

### Backend

1. Create a database (default is `taskmanager_db`).
2. Configure the backend (defaults are fine for local dev):

Environment variables supported by the backend:

- `DB_URL` (default: `jdbc:postgresql://localhost:5432/taskmanager_db`)
- `DB_USERNAME` (default: `postgres`)
- `DB_PASSWORD` (default: `postgres`)
- `JWT_SECRET` (default: a dev-only value; can be a plain string or Base64/Base64URL)
- `JWT_EXPIRATION` (default: `86400000` ms)
- `CORS_ALLOWED_ORIGINS` (default: `*`)

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

The Vite dev server proxies `/api` to `http://localhost:8080`.

If you host the backend elsewhere, set `VITE_API_BASE` (e.g. `https://your-api.example.com/api`).

## Database Migrations (Flyway)

- Flyway is enabled by default.
- Active migrations are in `backend/src/main/resources/db/migration/active/`.
- Hibernate auto-ddl is disabled (`spring.jpa.hibernate.ddl-auto=none`).

## Development Seed Data

When running with the `dev` profile, a dev-only seeder upserts demo users/teams/tasks so onboarding and E2E have stable credentials (even when using a persistent DB volume):

- `backend/src/main/java/com/taskmanager/api/config/DevDataSeeder.java`

Seeded demo credentials:

- `alice` / `password` (ROLE_USER)
- `bob` / `password` (ROLE_ADMIN + ROLE_USER)
- `charlie` / `password` (ROLE_USER)
- `dana` / `password` (ROLE_USER)

## Scripts

Backend helper scripts (write logs to `logs/`):

```bash
cd backend
./scripts/run-build-log.sh
./scripts/run-tests-log.sh
```

Repo health checks (writes a combined log to `logs/`):

```bash
./scripts/run-checks-log.sh --all

# or run individual checks
./scripts/run-checks-log.sh --frontend-lint
./scripts/run-checks-log.sh --backend-test
./scripts/run-checks-log.sh --compose-config
```

E2E helper script (writes logs to `logs/`):

```bash
./scripts/run-e2e-log.sh
```

Frontend scripts:

```bash
cd frontend
npm run dev
npm run build
npm run lint

# one-time browser install (CI runners do this automatically)
npm run e2e:install

# run Playwright smoke tests
npm run e2e
```

## E2E Smoke Tests (Playwright)

The smoke tests validate the main user flows (login, teams, tasks) in a real browser.

Prereqs:
- App running locally (recommended: `docker compose up` from repo root)
- Frontend reachable at `http://localhost:5173`
- Backend reachable at `http://localhost:8080`

Run:

```bash
cd frontend
npm run e2e:install
npm run e2e
```

Optional env vars:
- `E2E_USERNAME` / `E2E_PASSWORD` (defaults to the seeded `alice/password`)
- `PLAYWRIGHT_BASE_URL` (defaults to `http://localhost:5173`)

## Backend Integration Tests (Testcontainers)

Some backend integration tests use Testcontainers PostgreSQL so CI can run against a clean, real database.

- Requires Docker to be available on the machine running tests.
- Locally, tests are configured to skip when Docker isn’t available.

## Project Layout

See [STRUCTURE.md](STRUCTURE.md) for a detailed overview of the backend and frontend folders.

## Possible Future Improvements

- Add refresh tokens + token rotation (or switch to HttpOnly cookie auth)
- Add pagination/filtering/sorting for tasks and teams
- Add task comments, attachments, and audit history
- Add notifications (email or in-app) for due/overdue tasks
- Add real-time updates (WebSockets/SSE) for team/task changes
- Hardening: rate limiting, stricter CORS defaults, security headers, improved validation
- Observability: structured logging, metrics, tracing, health dashboards
- Production Docker images (multi-stage builds) + `docker compose` prod profile
- CI enhancements: coverage thresholds, dependency scanning, release/versioning
