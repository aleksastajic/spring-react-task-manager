# Task Manager - Full-Stack Application

A task management system built with Spring Boot (backend) and React (frontend).

## TaskManager API (Backend)

Spring Boot backend for user, team, and task management with JWT authentication and PostgreSQL.

### Quick Start

1. **Build and run:**
   ```bash
   cd backend
   ./mvnw spring-boot:run
   ```
2. **API Docs:**
   - Swagger UI: [http://localhost:8080/swagger-ui/index.html](http://localhost:8080/swagger-ui/index.html)
   - OpenAPI JSON: [http://localhost:8080/v3/api-docs](http://localhost:8080/v3/api-docs)

### Authentication Flow

1. Register a user via `/api/auth/register`.
2. Login via `/api/auth/login` to receive a JWT token.
3. Use the JWT token in the `Authorization: Bearer <token>` header for all protected endpoints.

### Example API Usage

#### Register
```bash
curl -X POST http://localhost:8080/api/auth/register \
  -H "Content-Type: application/json" \
  -d '{"username":"newuser","email":"newuser@example.com","password":"newpass","displayName":"New User"}'
```

#### Login
```bash
curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"usernameOrEmail":"newuser","password":"newpass"}'
```

#### Get Current User
```bash
TOKEN="<your-jwt-token>"
curl -H "Authorization: Bearer $TOKEN" http://localhost:8080/api/users/me
```

### API Endpoints Overview

See Swagger UI for full details and try out endpoints interactively.

**Auth**
- `POST /api/auth/register` — Register a new user
- `POST /api/auth/login` — Login and receive JWT

**Users**
- `GET /api/users/me` — Get current user profile (JWT required)

**Teams**
- `POST /api/teams` — Create a new team (USER role)
- `POST /api/teams/{teamId}/members/{userId}` — Add member to team (ADMIN role)
- `GET /api/teams?userId={userId}` — List teams for a user


**Tasks**
- `POST /api/tasks` — Create a new task (USER role)
- `GET /api/tasks/{id}` — Get task by ID
- `GET /api/tasks/team/{teamId}` — List tasks for a team
- `GET /api/tasks/user/{userId}` — List tasks assigned to a user
- `PATCH /api/tasks/{id}` — Update a task (title, description, due date, priority, status, assignees)
- `DELETE /api/tasks/{id}` — Delete a task (creator or admin)
- `POST /api/tasks/{taskId}/assignees/{userId}` — Assign user to task (admin or creator)
- `DELETE /api/tasks/{taskId}/assignees/{userId}` — Unassign user from task (admin, creator, or self)
- `PATCH /api/tasks/{taskId}/status` — Change task status (creator, admin, or assignee)

### Troubleshooting
- If Swagger UI loads but `/v3/api-docs` returns 500, check for dependency conflicts or exception handler issues.
- Use `springdoc-openapi-starter-webmvc-ui` version `2.5.0` with Spring Boot 3.2.x for compatibility.
- PostgreSQL must be running and accessible as configured in `application.properties`.


### Database Setup (PostgreSQL 16 & Flyway)

1. **Install PostgreSQL 16** (see your OS instructions).
2. **Initialize the data directory:**
  ```sh
  sudo -u postgres initdb -D /var/lib/pgsql/16/data
  ```
3. **Start the PostgreSQL 16 service:**
  ```sh
  sudo systemctl enable postgresql-16
  sudo systemctl start postgresql-16
  ```
  If the service file is missing, see Fedora troubleshooting below.
4. **Set the password for the postgres user:**
  ```sh
  sudo -u postgres psql -c "ALTER USER postgres PASSWORD 'postgres';"
  ```
5. **Create your application database:**
  ```sh
  sudo -u postgres createdb taskmanager_db
  ```
6. **Run your Spring Boot app.** Flyway will automatically apply migrations from `src/main/resources/db/migration`.

#### Fedora 43+ Troubleshooting
- If `postgresql-16.service` is missing, create a custom systemd unit or use `pg_ctl` directly:
  ```sh
  sudo -u postgres pg_ctl -D /var/lib/pgsql/16/data -l logfile start
  ```
- If you see permission errors, ensure `/var/lib/pgsql/16/data` is owned by `postgres`:
  ```sh
  sudo chown -R postgres:postgres /var/lib/pgsql/16/data
  ```
- If Flyway reports "Unsupported Database: PostgreSQL 18.x", downgrade to PostgreSQL 16.

### Recent Backend Changes

## Migration Management
- **Flyway removed:** The project no longer uses Flyway for database migrations due to compatibility issues with newer PostgreSQL versions.
- **Hibernate auto-ddl restored:** Schema updates are now managed by Hibernate (`spring.jpa.hibernate.ddl-auto=update`). No migration scripts are required.

## CORS Configuration
- **Global CORS enabled:** The backend now allows cross-origin requests from any origin, method, and header for development. This enables integration with the React frontend and other clients.
- **How it works:**
  - See `src/main/java/com/taskmanager/api/config/CorsConfig.java` for the configuration.
  - You can restrict allowed origins in production by editing this file.

## Next Steps
- You can now start building the React frontend and connect it to the backend API.
- For production, review and restrict CORS settings as needed.

---

## Frontend (React + Vite)

The frontend is built with React and Vite. It is located in the `frontend/` directory.

### Quick Start

1. **Install dependencies:**
   ```bash
   cd frontend
   npm install
   ```
2. **Run the development server:**
   ```bash
   npm run dev
   ```
   The app will be available at [http://localhost:5173](http://localhost:5173) by default.

### Connecting to the Backend
- The backend API runs at [http://localhost:8080](http://localhost:8080).
- CORS is enabled, so you can make API requests from the frontend during development.
- Update API URLs in your React code as needed.

### Project Structure
- `frontend/src/` — React source files
- `frontend/public/` — Static assets
- `frontend/index.html` — Main HTML entry point

---

### Project Structure
See [STRUCTURE.md](STRUCTURE.md) for a detailed backend package and file overview.
