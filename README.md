# 🔄 WAMS — Workflow & Approval Management System

<p align="center">
  <img src="https://img.shields.io/badge/Java-17-orange?style=for-the-badge&logo=openjdk&logoColor=white" alt="Java 17"/>
  <img src="https://img.shields.io/badge/Spring_Boot-3.5-6DB33F?style=for-the-badge&logo=spring-boot&logoColor=white" alt="Spring Boot"/>
  <img src="https://img.shields.io/badge/Maven-C71A36?style=for-the-badge&logo=apache-maven&logoColor=white" alt="Maven"/>
  <img src="https://img.shields.io/badge/JWT-Auth-000000?style=for-the-badge&logo=jsonwebtokens&logoColor=white" alt="JWT"/>
  <img src="https://img.shields.io/badge/H2-Database-003545?style=for-the-badge" alt="H2"/>
</p>

<p align="center">
  A production-grade backend system for managing multi-step approval workflows with role-based access control, JWT authentication, and a state-machine driven workflow engine.
</p>

---

## ✨ Features

- **🔐 JWT Authentication** — Secure register/login with BCrypt password hashing
- **👥 Role-Based Access Control** — Three roles: Requester, Reviewer, Approver
- **⚙️ Workflow State Machine** — Enforced state transitions with role validation
- **✅ Bean Validation** — Input validation with clear error messages
- **🛡️ Global Exception Handling** — Consistent JSON error responses (400, 404, 409, 500)
- **📦 DTO Pattern** — Clean separation between API contracts and internal entities
- **🗄️ JPA Persistence** — Database-backed storage with H2 (dev) / MySQL (prod)
- **🧪 Comprehensive Tests** — 13 unit tests with MockMvc and Spring Security Test

## 🏗️ Tech Stack

| Layer | Technology |
|-------|-----------|
| **Language** | Java 17 |
| **Framework** | Spring Boot 3.5.10 |
| **Security** | Spring Security + JWT (JJWT 0.12.6) |
| **Database** | H2 (dev) / MySQL (prod) |
| **ORM** | Spring Data JPA / Hibernate |
| **Validation** | Jakarta Bean Validation |
| **Build** | Maven |
| **Testing** | JUnit 5, Mockito, Spring Security Test |

## 🏛️ Architecture

```
┌─────────────────────────────────────────────────────────────────┐
│                        Client (REST)                            │
└──────────────────────────┬──────────────────────────────────────┘
                           │ HTTP + JWT Bearer Token
                           ▼
┌─────────────────────────────────────────────────────────────────┐
│  JwtAuthenticationFilter  │  SecurityConfig (Filter Chain)      │
│  Extract & validate JWT   │  Endpoint authorization rules       │
└──────────────────────────┬──────────────────────────────────────┘
                           ▼
┌──────────────────┐  ┌──────────────────┐  ┌────────────────────┐
│  AuthController  │  │ WorkflowController│  │  HealthController  │
│  /api/auth/*     │  │  /api/workflows/* │  │  /api/health       │
└────────┬─────────┘  └────────┬─────────┘  └────────────────────┘
         │                     │
         ▼                     ▼
┌──────────────────┐  ┌──────────────────┐
│   JwtService     │  │ WorkflowService  │  ← Business Logic
│   PasswordEncoder│  │ State Machine    │
└────────┬─────────┘  └────────┬─────────┘
         │                     │
         ▼                     ▼
┌──────────────────┐  ┌──────────────────┐
│  UserRepository  │  │WorkflowRepository│  ← Spring Data JPA
└────────┬─────────┘  └────────┬─────────┘
         │                     │
         ▼                     ▼
┌─────────────────────────────────────────────────────────────────┐
│                    H2 / MySQL Database                          │
└─────────────────────────────────────────────────────────────────┘
```

## 🔄 Workflow State Machine

Workflows follow a strict state machine with role-based transitions:

```
DRAFT ──(REQUESTER)──▶ SUBMITTED ──(REVIEWER)──▶ UNDER_REVIEW
                                                       │
                                             ┌─────────┴─────────┐
                                        (APPROVER)          (APPROVER)
                                             ▼                   ▼
                                         APPROVED            REJECTED
```

| Transition | Required Role | HTTP Status on Failure |
|-----------|---------------|----------------------|
| DRAFT → SUBMITTED | `REQUESTER` | 409 Conflict |
| SUBMITTED → UNDER_REVIEW | `REVIEWER` | 409 Conflict |
| UNDER_REVIEW → APPROVED | `APPROVER` | 409 Conflict |
| UNDER_REVIEW → REJECTED | `APPROVER` | 409 Conflict |

## 📡 API Reference

### Authentication

| Method | Endpoint | Description | Auth |
|--------|----------|-------------|------|
| `POST` | `/api/auth/register` | Register a new user | Public |
| `POST` | `/api/auth/login` | Login and receive JWT | Public |

<details>
<summary><b>POST /api/auth/register</b></summary>

**Request:**
```json
{
  "username": "john_doe",
  "password": "secret123",
  "role": "REQUESTER"
}
```

**Response (201 Created):**
```json
{
  "token": "eyJhbGciOiJIUzI1NiJ9...",
  "username": "john_doe",
  "role": "REQUESTER"
}
```
</details>

<details>
<summary><b>POST /api/auth/login</b></summary>

**Request:**
```json
{
  "username": "john_doe",
  "password": "secret123"
}
```

**Response (200 OK):**
```json
{
  "token": "eyJhbGciOiJIUzI1NiJ9...",
  "username": "john_doe",
  "role": "REQUESTER"
}
```
</details>

### Workflows

| Method | Endpoint | Description | Auth |
|--------|----------|-------------|------|
| `GET` | `/api/workflows` | List all workflows | JWT |
| `GET` | `/api/workflows/{id}` | Get workflow by ID | JWT |
| `POST` | `/api/workflows` | Create new workflow | JWT |
| `PATCH` | `/api/workflows/{id}/transition` | Transition workflow status | JWT |

<details>
<summary><b>POST /api/workflows</b></summary>

**Headers:** `Authorization: Bearer <token>`

**Request:**
```json
{
  "title": "Leave Request",
  "description": "Employee leave approval workflow"
}
```

**Response (201 Created):**
```json
{
  "id": 1,
  "title": "Leave Request",
  "description": "Employee leave approval workflow",
  "status": "DRAFT",
  "createdAt": "2026-02-25T20:00:00"
}
```
</details>

<details>
<summary><b>PATCH /api/workflows/{id}/transition</b></summary>

**Headers:** `Authorization: Bearer <token>`

**Request:**
```json
{
  "targetStatus": "SUBMITTED",
  "role": "REQUESTER"
}
```

**Response (200 OK):**
```json
{
  "id": 1,
  "title": "Leave Request",
  "description": "Employee leave approval workflow",
  "status": "SUBMITTED",
  "createdAt": "2026-02-25T20:00:00"
}
```
</details>

### Error Responses

All errors follow a consistent format:

```json
{
  "timestamp": "2026-02-25T20:00:00",
  "status": 404,
  "error": "Not Found",
  "message": "Workflow not found with id: 42",
  "path": "/api/workflows/42"
}
```

| Status | Meaning |
|--------|---------|
| `400` | Validation failure (missing/invalid fields) |
| `401` | Invalid credentials (login) |
| `403` | Missing or invalid JWT token |
| `404` | Resource not found |
| `409` | Invalid state transition or duplicate username |
| `500` | Unexpected server error |

## 🚀 Getting Started

### Prerequisites

- **Java 17** — [Download](https://www.oracle.com/java/technologies/javase/jdk17-archive-downloads.html)
- **Maven 3.8+** — or use the included Maven Wrapper (`./mvnw`)

### Run the Application

```bash
# Clone the repository
git clone https://github.com/prakash-nitc/wams-backend.git
cd wams-backend

# Run (Maven Wrapper — no Maven install required)
./mvnw spring-boot:run

# Or on Windows
.\mvnw spring-boot:run
```

The server starts at **http://localhost:8080**

### Run Tests

```bash
./mvnw test
```

### Quick Test with cURL

```bash
# 1. Register a user
curl -X POST http://localhost:8080/api/auth/register \
  -H "Content-Type: application/json" \
  -d '{"username":"alice","password":"secret123","role":"REQUESTER"}'

# 2. Use the returned token for authenticated requests
curl http://localhost:8080/api/workflows \
  -H "Authorization: Bearer <your-token>"

# 3. Create a workflow
curl -X POST http://localhost:8080/api/workflows \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer <your-token>" \
  -d '{"title":"Leave Request","description":"Annual leave approval"}'
```

### H2 Database Console

Access the in-memory database browser at: **http://localhost:8080/h2-console**

| Setting | Value |
|---------|-------|
| JDBC URL | `jdbc:h2:mem:wams` |
| Username | `sa` |
| Password | *(empty)* |

## 📁 Project Structure

```
src/main/java/com/nit/arwms/
├── ArwmsApplication.java              # Spring Boot entry point
│
├── auth/                              # 🔐 Authentication & Security
│   ├── Role.java                      # Enum: REQUESTER, REVIEWER, APPROVER
│   ├── User.java                      # JPA Entity + UserDetails
│   ├── UserRepository.java            # Spring Data repository
│   ├── JwtService.java                # JWT token generation & validation
│   ├── JwtAuthenticationFilter.java   # Per-request JWT filter
│   ├── SecurityConfig.java            # Security filter chain + BCrypt
│   ├── AuthController.java            # POST /api/auth/register, /login
│   ├── AuthRequest.java               # Login DTO
│   ├── AuthResponse.java              # Token response DTO
│   └── RegisterRequest.java           # Registration DTO
│
├── workflow/                          # ⚙️ Core Workflow Engine
│   ├── Workflow.java                  # JPA Entity
│   ├── WorkflowStatus.java            # State machine enum
│   ├── WorkflowRepository.java        # Spring Data repository
│   ├── WorkflowService.java           # Business logic + transitions
│   ├── WorkflowController.java        # REST endpoints
│   ├── WorkflowRequest.java           # Create workflow DTO
│   ├── WorkflowResponse.java          # Response DTO (record)
│   └── WorkflowTransitionRequest.java # Transition DTO
│
├── exception/                         # 🛡️ Error Handling
│   ├── GlobalExceptionHandler.java    # @RestControllerAdvice
│   ├── ErrorResponse.java             # Standardized error format
│   ├── WorkflowNotFoundException.java # 404 exception
│   └── InvalidTransitionException.java# 409 exception
│
└── system/                            # ❤️ System
    ├── HealthController.java          # GET /api/health
    └── HealthResponse.java            # Health check DTO
```

## 📋 Development Phases

This project was built incrementally, phase by phase:

| Phase | Topic | Status |
|-------|-------|--------|
| 1 | Project Setup (Spring Initializr) | ✅ |
| 2 | REST API Fundamentals | ✅ |
| 3 | Service Layer Pattern | ✅ |
| 4 | Database Persistence (JPA) | ✅ |
| 5 | DTOs & API Design | ✅ |
| 6 | Exception Handling | ✅ |
| 7 | Core Workflow Logic (State Machine) | ✅ |
| 8 | Security (JWT + RBAC) | ✅ |
| 9 | Docker | 🔲 |

Detailed documentation for each phase is available in the [`docs/`](docs/) directory.

## 📄 License

This project is for educational purposes as part of the NIT Calicut curriculum.

---

<p align="center">
  Built with ☕ and Spring Boot
</p>
