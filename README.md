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
