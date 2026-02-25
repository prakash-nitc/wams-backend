# Phase 8 — Security

**Status:** ✅ Completed  
**Date:** 2026-02-25

## Objective

Secure APIs with JWT authentication and enforce role-based access control.

## What Was Implemented

### Files Created

| File | Purpose |
|------|---------|
| `auth/Role.java` | Enum: REQUESTER, REVIEWER, APPROVER |
| `auth/User.java` | JPA Entity implementing Spring Security's UserDetails |
| `auth/UserRepository.java` | Repository with findByUsername, existsByUsername |
| `auth/JwtService.java` | JWT token generation and validation (JJWT library) |
| `auth/AuthRequest.java` | Login DTO (username, password) |
| `auth/AuthResponse.java` | Response DTO (token, username, role) |
| `auth/RegisterRequest.java` | Registration DTO (username, password, role) |
| `auth/AuthController.java` | POST /api/auth/register and /api/auth/login |
| `auth/JwtAuthenticationFilter.java` | Extracts JWT from Authorization header on every request |
| `auth/SecurityConfig.java` | Security filter chain + BCrypt + endpoint rules |

### Files Modified

| File | Change |
|------|--------|
| `pom.xml` | Added spring-boot-starter-security, JJWT, spring-security-test |
| `application.properties` | Added jwt.secret and jwt.expiration-ms |
| `WorkflowController.java` | Transition uses authenticated user's role |
| `WorkflowControllerTest.java` | @SpringBootTest + @WithMockUser + 403 test |
| `HealthControllerTest.java` | @SpringBootTest + @AutoConfigureMockMvc |

### Key Concepts Learned

#### 1. Authentication vs Authorization
- **Authentication** = "Who are you?" (login with credentials)
- **Authorization** = "What can you do?" (role-based access)

#### 2. JWT (JSON Web Token)
```
Header.Payload.Signature
```
- Self-contained: carries username + role + expiry
- Stateless: no server-side sessions needed
- Client sends it in header: `Authorization: Bearer <token>`

#### 3. Security Filter Chain
```
HTTP Request → JwtAuthenticationFilter → SecurityConfig Rules → Controller
                    ↓
            Extract Bearer token
            Validate signature + expiry
            Load User from DB
            Set SecurityContext
```

#### 4. Endpoint Security Rules
| Endpoint | Access |
|----------|--------|
| `POST /api/auth/**` | Public |
| `GET /api/health` | Public |
| `/h2-console/**` | Public |
| `GET /api/workflows/**` | Authenticated |
| `POST /api/workflows` | Authenticated |
| `PATCH /api/workflows/*/transition` | Authenticated |

#### 5. BCrypt Password Hashing
Passwords never stored in plain text. BCrypt adds a random salt.

#### 6. @WithMockUser (Testing)
```java
@Test
@WithMockUser(roles = "REQUESTER")
void transitionWorkflow_submitsSuccessfully() { ... }
```
Creates a fake authenticated user for tests without needing real JWT tokens.

## API Reference

| Method | Endpoint | Description | Auth |
|--------|----------|-------------|------|
| POST | `/api/auth/register` | Register user | Public |
| POST | `/api/auth/login` | Login, get JWT | Public |
| GET | `/api/workflows` | List workflows | JWT |
| GET | `/api/workflows/{id}` | Get by ID | JWT |
| POST | `/api/workflows` | Create workflow | JWT |
| PATCH | `/api/workflows/{id}/transition` | Change status | JWT |

## Verification Results

All 13 tests passed:
- `ArwmsApplicationTests.contextLoads()` ✅
- `HealthControllerTest.returnsHealthStatus()` ✅ (public endpoint)
- `WorkflowControllerTest` (11 tests) ✅
  - Authenticated: GET, POST, PATCH, validation, transitions
  - Unauthenticated: returns 403 Forbidden

## Next Phase

**Phase 9 — Docker**  
Containerize the WAMS backend with a Dockerfile.
