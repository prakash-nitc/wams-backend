# Phase 4 — Database and Persistence (JPA)

**Status:** ✅ Completed  
**Date:** 2026-02-14

## Objective

Persist workflow data using a relational database with JPA and Hibernate.

## What Was Implemented

### Files Created/Modified

| File | Action | Purpose |
|------|--------|---------|
| `Workflow.java` | Modified | Added JPA entity annotations (@Entity, @Id, @GeneratedValue, @Column) |
| `WorkflowRepository.java` | Created | JPA repository interface extending JpaRepository |
| `WorkflowService.java` | Modified | Refactored from ArrayList to use WorkflowRepository |
| `WorkflowControllerTest.java` | Modified | Updated to use @MockitoBean for service mocking |
| `pom.xml` | Modified | Added H2 in-memory database dependency |
| `application.properties` | Modified | Added H2 and JPA/Hibernate configuration |

### Key Concepts Learned

#### 1. JPA Entity Annotations
```java
@Entity
@Table(name = "workflows")
public class Workflow {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String title;
}
```

#### 2. Spring Data JPA Repository
```java
public interface WorkflowRepository extends JpaRepository<Workflow, Long> {
    // Spring auto-generates: findAll(), findById(), save(), deleteById(), count()
}
```

#### 3. Service → Repository Wiring
```java
@Service
public class WorkflowService {
    private final WorkflowRepository workflowRepository;

    public WorkflowService(WorkflowRepository workflowRepository) {
        this.workflowRepository = workflowRepository;
    }

    public Workflow createWorkflow(Workflow workflow) {
        workflow.setStatus("DRAFT");
        workflow.setCreatedAt(LocalDateTime.now());
        return workflowRepository.save(workflow); // Persists to database
    }
}
```

#### 4. @MockitoBean (Spring Boot 3.4+)
- Replaces deprecated `@MockBean`
- Creates Mockito mock of the service in test context
- Allows isolated controller testing without JPA dependencies

## Architecture After Phase 4

```
HTTP Request
     ↓
┌─────────────────────┐
│  WorkflowController │  ← HTTP handling
└─────────┬───────────┘
          │ delegates
┌─────────────────────┐
│   WorkflowService   │  ← Business logic
└─────────┬───────────┘
          │ uses
┌─────────────────────┐
│ WorkflowRepository  │  ← Data access (JPA)
└─────────┬───────────┘
          │ SQL
┌─────────────────────┐
│    H2 Database      │  ← In-memory database
└─────────────────────┘
```

## Verification Results

All 6 tests passed:
- `ArwmsApplicationTests.contextLoads()` ✅
- `HealthControllerTest.returnsHealthStatus()` ✅
- `WorkflowControllerTest.getAllWorkflows_returnsEmptyListInitially()` ✅
- `WorkflowControllerTest.createWorkflow_returnsCreatedWorkflow()` ✅
- `WorkflowControllerTest.getWorkflowById_returnsWorkflowWhenFound()` ✅
- `WorkflowControllerTest.getWorkflowById_returnsNotFoundForNonExistentId()` ✅

## Next Phase

**Phase 5 — DTOs and API Design**  
Design clean and safe API contracts using DTOs, separating request/response models from entities.
