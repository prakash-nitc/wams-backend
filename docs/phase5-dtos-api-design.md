# Phase 5 — DTOs and API Design

**Status:** ✅ Completed  
**Date:** 2026-02-14

## Objective

Design clean and safe API contracts using DTOs, separating request/response models from entities.

## What Was Implemented

### Files Created/Modified

| File | Action | Purpose |
|------|--------|---------|
| `WorkflowRequest.java` | Created | Input DTO with validation (@NotBlank, @Size) |
| `WorkflowResponse.java` | Created | Output DTO as Java record with factory method |
| `WorkflowController.java` | Modified | Uses DTOs + @Valid for request validation |
| `WorkflowService.java` | Modified | Accepts WorkflowRequest, returns WorkflowResponse |
| `WorkflowControllerTest.java` | Modified | Tests validation (blank title → 400), DTO responses |
| `pom.xml` | Modified | Added spring-boot-starter-validation |

### Key Concepts Learned

#### 1. DTO Pattern (Data Transfer Object)
Separates API contracts from internal entities:
- **WorkflowRequest** — What the client sends (only title, description)
- **WorkflowResponse** — What the client receives (id, title, description, status, createdAt)
- **Workflow (Entity)** — Internal database representation

Why?
- Clients can't set fields they shouldn't (id, status, createdAt)
- Entity changes don't break the API
- Validation rules are clear on the request side

#### 2. Bean Validation Annotations
```java
@NotBlank(message = "Title is required")
@Size(max = 100, message = "Title must be at most 100 characters")
private String title;
```

#### 3. @Valid Annotation
```java
@PostMapping
public ResponseEntity<WorkflowResponse> createWorkflow(
        @Valid @RequestBody WorkflowRequest request) { ... }
```
Triggers automatic validation. Invalid input → 400 Bad Request.

#### 4. Java Records for DTOs
```java
public record WorkflowResponse(Long id, String title, String description,
        String status, LocalDateTime createdAt) {

    public static WorkflowResponse fromEntity(Workflow workflow) { ... }
}
```
Records are ideal for response DTOs — immutable, concise, auto-generates getters/equals/hashCode.

## Architecture After Phase 5

```
Client Request (JSON)
     ↓
┌─────────────────────┐
│  WorkflowRequest    │  ← Input DTO (validated)
└─────────┬───────────┘
          ↓
┌─────────────────────┐
│ WorkflowController  │  ← @Valid + delegates
└─────────┬───────────┘
          ↓
┌─────────────────────┐
│  WorkflowService    │  ← DTO ↔ Entity conversion
└─────────┬───────────┘
          ↓
┌─────────────────────┐
│ WorkflowRepository  │  ← JPA persistence
└─────────┬───────────┘
          ↓
┌─────────────────────┐
│    WorkflowResponse │  ← Output DTO
└─────────────────────┘
     ↓
Client Response (JSON)
```

## Verification Results

All 8 tests passed:
- `ArwmsApplicationTests.contextLoads()` ✅
- `HealthControllerTest.returnsHealthStatus()` ✅
- `WorkflowControllerTest.getAllWorkflows_returnsEmptyListInitially()` ✅
- `WorkflowControllerTest.getAllWorkflows_returnsWorkflowList()` ✅
- `WorkflowControllerTest.createWorkflow_returnsCreatedWorkflow()` ✅
- `WorkflowControllerTest.createWorkflow_returnsBadRequestWhenTitleIsBlank()` ✅
- `WorkflowControllerTest.createWorkflow_returnsBadRequestWhenTitleIsMissing()` ✅
- `WorkflowControllerTest.getWorkflowById_returnsWorkflowWhenFound()` ✅
- `WorkflowControllerTest.getWorkflowById_returnsNotFoundForNonExistentId()` ✅

## Next Phase

**Phase 6 — Exception Handling**  
Handle failures gracefully with custom exceptions, global exception handler, and proper HTTP status codes.
