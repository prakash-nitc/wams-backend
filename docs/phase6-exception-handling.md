# Phase 6 — Exception Handling

**Status:** ✅ Completed  
**Date:** 2026-02-16

## Objective

Handle failures gracefully and consistently across the entire API.

## What Was Implemented

### Files Created/Modified

| File | Action | Purpose |
|------|--------|---------|
| `exception/ErrorResponse.java` | Created | Standardized error response record |
| `exception/WorkflowNotFoundException.java` | Created | Custom 404 exception |
| `exception/GlobalExceptionHandler.java` | Created | @RestControllerAdvice global handler |
| `WorkflowService.java` | Modified | findById throws exception instead of Optional |
| `WorkflowController.java` | Modified | Simplified — no error logic, delegates to service |
| `WorkflowControllerTest.java` | Modified | Tests verify error response body structure |

### Key Concepts Learned

#### 1. Custom Exceptions
```java
public class WorkflowNotFoundException extends RuntimeException {
    public WorkflowNotFoundException(Long id) {
        super("Workflow not found with id: " + id);
    }
}
```
- Extends RuntimeException (unchecked)
- Carries a meaningful message
- Caught by GlobalExceptionHandler → HTTP 404

#### 2. @RestControllerAdvice (Global Exception Handler)
```java
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(WorkflowNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleWorkflowNotFound(...) { ... }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidationErrors(...) { ... }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGenericException(...) { ... }
}
```

#### 3. Standardized Error Responses
Every error returns the same JSON shape:
```json
{
    "timestamp": "2026-02-16T11:00:00",
    "status": 404,
    "error": "Not Found",
    "message": "Workflow not found with id: 42",
    "path": "/api/workflows/42"
}
```

#### 4. HTTP Status Codes Used
| Code | Meaning | When |
|------|---------|------|
| 200 | OK | Successful GET |
| 201 | Created | Successful POST |
| 400 | Bad Request | Validation failure (@Valid) |
| 404 | Not Found | Workflow doesn't exist |
| 500 | Internal Server Error | Catch-all for unexpected errors |

## Architecture After Phase 6

```
Controller throws exception
        ↓
┌──────────────────────────┐
│  GlobalExceptionHandler  │  ← @RestControllerAdvice
│  @ExceptionHandler(...)  │
└───────────┬──────────────┘
            ↓
┌──────────────────────────┐
│      ErrorResponse       │  ← Consistent JSON shape
│  (timestamp, status,     │
│   error, message, path)  │
└──────────────────────────┘
```

## Verification Results

All 9 tests passed:
- `ArwmsApplicationTests.contextLoads()` ✅
- `HealthControllerTest.returnsHealthStatus()` ✅
- `WorkflowControllerTest.getAllWorkflows_returnsEmptyListInitially()` ✅
- `WorkflowControllerTest.getAllWorkflows_returnsWorkflowList()` ✅
- `WorkflowControllerTest.createWorkflow_returnsCreatedWorkflow()` ✅
- `WorkflowControllerTest.createWorkflow_returnsBadRequestWithErrorBody()` ✅
- `WorkflowControllerTest.createWorkflow_returnsBadRequestWhenTitleIsMissing()` ✅
- `WorkflowControllerTest.getWorkflowById_returnsWorkflowWhenFound()` ✅
- `WorkflowControllerTest.getWorkflowById_returnsNotFoundWithErrorBody()` ✅

## Next Phase

**Phase 7 — Core Workflow Logic**  
Implement workflow states, state transitions, approval rules, and role-based actions.
