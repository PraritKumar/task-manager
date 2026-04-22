# Task Manager API

A production-grade RESTful API for task management built with **Java 21**, **Spring Boot 3**, **PostgreSQL**, **Docker**, and **Kubernetes**.

---

## Tech Stack

| Layer | Technology |
|---|---|
| Language | Java 21 |
| Framework | Spring Boot 3.2, Spring Data JPA, Spring Validation |
| Database | PostgreSQL 16 |
| Caching | (Redis — Phase 2) |
| Containerisation | Docker, Docker Compose |
| Orchestration | Kubernetes |
| Testing | JUnit 5, Mockito |
| API Docs | Swagger UI (SpringDoc OpenAPI) |
| Build Tool | Maven |

---

## Architecture

```
Client (Postman / Browser)
        │
        ▼
  TaskController          ← Routing layer (HTTP in/out)
        │
        ▼
  TaskService (interface)
        │
        ▼
  TaskServiceImpl         ← Business logic layer
        │
        ▼
  TaskRepository          ← Data access layer (Spring Data JPA)
        │
        ▼
  PostgreSQL DB
```

**Package structure:**
```
com.prarit.taskmanager/
├── controller/     → REST endpoints
├── service/        → Business logic (interface + implementation)
├── repository/     → Database operations
├── model/          → JPA entity (maps to DB table)
├── dto/            → Request/Response objects (decouples API from DB)
└── exception/      → Custom exceptions + global error handler
```

---

## API Endpoints

| Method | Endpoint | Description |
|---|---|---|
| `POST` | `/api/v1/tasks` | Create a new task |
| `GET` | `/api/v1/tasks` | Get all tasks |
| `GET` | `/api/v1/tasks?status=TODO` | Filter by status |
| `GET` | `/api/v1/tasks?priority=HIGH` | Filter by priority |
| `GET` | `/api/v1/tasks?search=keyword` | Search by title |
| `GET` | `/api/v1/tasks/{id}` | Get task by ID |
| `PUT` | `/api/v1/tasks/{id}` | Update a task |
| `PATCH` | `/api/v1/tasks/{id}/status?status=DONE` | Update status only |
| `DELETE` | `/api/v1/tasks/{id}` | Delete a task |

**Task Status values:** `TODO` · `IN_PROGRESS` · `DONE`

**Task Priority values:** `LOW` · `MEDIUM` · `HIGH`

---

## Running the App

### Option 1 — Docker Compose (Recommended)
Runs both the app and PostgreSQL in containers. No local setup needed.

```bash
# Clone the repo
git clone https://github.com/PraritKumar/task-manager.git
cd task-manager

# Start everything
docker-compose up --build

# App is live at: http://localhost:8080
# Swagger UI at:  http://localhost:8080/swagger-ui.html
```

### Option 2 — Local (IntelliJ)
Requires PostgreSQL installed locally.

```bash
# Update application.yml — change "db" to "localhost" in the datasource URL
# Then run:
./mvnw spring-boot:run
```

---

## Running Tests

```bash
./mvnw test
```

Tests cover: create task, get by ID, not-found exception, get all tasks, delete success, delete not-found.

---

## Kubernetes Deployment

```bash
# Apply all manifests
kubectl apply -f k8s/configmap.yaml
kubectl apply -f k8s/service.yaml
kubectl apply -f k8s/deployment.yaml

# Check pods are running
kubectl get pods

# Access the app
# http://<node-ip>:30080/api/v1/tasks
```

---

## Sample Request

**Create a task:**
```bash
curl -X POST http://localhost:8080/api/v1/tasks \
  -H "Content-Type: application/json" \
  -d '{
    "title": "Review PR for payment service",
    "description": "Check the Redis caching implementation",
    "priority": "HIGH"
  }'
```

**Response:**
```json
{
  "id": 1,
  "title": "Review PR for payment service",
  "description": "Check the Redis caching implementation",
  "status": "TODO",
  "priority": "HIGH",
  "createdAt": "2025-04-21T10:30:00",
  "updatedAt": "2025-04-21T10:30:00"
}
```
