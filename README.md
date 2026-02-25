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
