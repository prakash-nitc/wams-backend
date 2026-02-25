# Phase 7 — Core Workflow Logic

**Status:** ✅ Completed  
**Date:** 2026-02-25

## Objective

Implement the heart of WAMS: workflow states, state transitions, approval rules, and role-based actions.

## What Was Implemented

### Files Created/Modified

| File | Action | Purpose |
|------|--------|---------|
| `WorkflowStatus.java` | Created | Enum with state machine (transitions + role rules) |
| `WorkflowTransitionRequest.java` | Created | DTO for transition API requests |
| `InvalidTransitionException.java` | Created | Custom exception for illegal transitions |
| `Workflow.java` | Modified | Status field changed from String to enum |
| `WorkflowService.java` | Modified | Added `transitionWorkflow()` with validation |
| `WorkflowController.java` | Modified | Added `PATCH /{id}/transition` endpoint |
| `WorkflowResponse.java` | Modified | Converts enum to string in API response |
| `GlobalExceptionHandler.java` | Modified | Added 409 Conflict + bad JSON handlers |
| `WorkflowControllerTest.java` | Modified | Added 5 transition tests |

### Key Concepts Learned

#### 1. State Machine with Enums
```java
public enum WorkflowStatus {
    DRAFT, SUBMITTED, UNDER_REVIEW, APPROVED, REJECTED;

    public boolean canTransitionTo(WorkflowStatus target) { ... }
    public String requiredRoleFor(WorkflowStatus target) { ... }
}
```

#### 2. State Flow Diagram
```
DRAFT ──(REQUESTER)──→ SUBMITTED ──(REVIEWER)──→ UNDER_REVIEW
                                                      │
                                            ┌─────────┴─────────┐
                                       (APPROVER)          (APPROVER)
                                            ↓                   ↓
                                        APPROVED            REJECTED
```

#### 3. Role-Based Actions
| Transition | Required Role |
|-----------|---------------|
| DRAFT → SUBMITTED | REQUESTER |
| SUBMITTED → UNDER_REVIEW | REVIEWER |
| UNDER_REVIEW → APPROVED | APPROVER |
| UNDER_REVIEW → REJECTED | APPROVER |

#### 4. @Enumerated(EnumType.STRING)
Stores enum as readable text in DB (e.g., "DRAFT") instead of ordinal number.

#### 5. PATCH Endpoint
```
PATCH /api/workflows/{id}/transition
{
    "targetStatus": "SUBMITTED",
    "role": "REQUESTER"
}
```

#### 6. HTTP 409 Conflict
Used for invalid state transitions — the request conflicts with the current resource state.

## API Reference

| Method | Endpoint | Description | Status Codes |
|--------|----------|-------------|-------------|
| GET | `/api/workflows` | List all | 200 |
| GET | `/api/workflows/{id}` | Get by ID | 200, 404 |
| POST | `/api/workflows` | Create | 201, 400 |
| PATCH | `/api/workflows/{id}/transition` | Change status | 200, 400, 404, 409 |

## Verification Results

All 12 tests passed:
- `ArwmsApplicationTests.contextLoads()` ✅
- `HealthControllerTest.returnsHealthStatus()` ✅
- `WorkflowControllerTest` (10 tests) ✅
  - GET, POST, validation, not-found
  - Transition: submit, approve, invalid transition (409), unauthorized role (409), non-existent (404)

## Next Phase

**Phase 8 — Security**  
Secure APIs with authentication (JWT) and enforce role-based access control.
