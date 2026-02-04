# Phase 2 — REST API Fundamentals

**Status:** ✅ Completed  
**Date:** 2026-02-05

## Objective

Expose the first REST APIs and understand request–response flow.

## What Was Implemented

### Files Created

| File | Purpose |
|------|---------|
| `src/main/java/com/nit/arwms/workflow/Workflow.java` | Domain model for workflows |
| `src/main/java/com/nit/arwms/workflow/WorkflowController.java` | REST controller with CRUD endpoints |
| `src/test/java/com/nit/arwms/workflow/WorkflowControllerTest.java` | MockMvc-based unit tests |

### API Endpoints

| Method | Endpoint | Description |
|--------|----------|-------------|
| GET | `/api/workflows` | List all workflows |
| GET | `/api/workflows/{id}` | Get workflow by ID |
| POST | `/api/workflows` | Create a new workflow |
| GET | `/api/health` | Health check (from Phase 1) |

### Key Concepts Learned

1. **@RestController** — Marks a class as a REST API handler
2. **@RequestMapping** — Sets the base URL path
3. **@GetMapping / @PostMapping** — Maps HTTP methods to Java methods
4. **@PathVariable** — Extracts values from URL path
5. **@RequestBody** — Deserializes JSON request body to Java object
6. **ResponseEntity** — Allows control over HTTP status codes

### Workflow Model

```java
public class Workflow {
    private Long id;
    private String title;
    private String description;
    private String status;        // DRAFT, PENDING, APPROVED
    private LocalDateTime createdAt;
}
```

## Verification Results

### Automated Tests
All tests passed:
- `HealthControllerTest.returnsHealthStatus()` ✅
- `WorkflowControllerTest.getAllWorkflows_returnsEmptyListInitially()` ✅
- `WorkflowControllerTest.createWorkflow_returnsCreatedWorkflow()` ✅
- `WorkflowControllerTest.getWorkflowById_returnsNotFoundForNonExistentId()` ✅

### Manual Testing

**GET /api/workflows** (empty list):
```
[]
```

**POST /api/workflows** (create workflow):
```json
{
    "id": 1,
    "title": "Leave Request",
    "description": "Employee leave approval workflow",
    "status": "DRAFT",
    "createdAt": "2026-02-05T00:44:09.6663631"
}
```

**GET /api/health**:
```json
{"status": "UP", "service": "wams"}
```

## Next Phase

**Phase 3 — Service Layer and Business Logic**  
Separate business logic from HTTP handling using `@Service` and dependency injection.
