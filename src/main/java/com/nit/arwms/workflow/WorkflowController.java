package com.nit.arwms.workflow;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * REST Controller for Workflow operations.
 * 
 * Demonstrates:
 * - @RestController annotation
 * - @GetMapping and @PostMapping
 * - @PathVariable and @RequestBody
 * - JSON request/response handling
 * - Constructor injection (Phase 3)
 */
@RestController
@RequestMapping("/api/workflows")
public class WorkflowController {

    private final WorkflowService workflowService;

    /**
     * Constructor Injection - Spring automatically injects WorkflowService.
     * 
     * Key Concept: Why Constructor Injection?
     * ---------------------------------------
     * 1. Dependencies are EXPLICIT and REQUIRED (compile-time safety)
     * 2. Fields can be FINAL (immutability)
     * 3. Easier to TEST (can inject mocks in unit tests)
     * 4. No reflection magic needed (unlike @Autowired on fields)
     * 
     * Note: When there's only ONE constructor, @Autowired is optional.
     * Spring will automatically use it for dependency injection.
     */
    public WorkflowController(WorkflowService workflowService) {
        this.workflowService = workflowService;
    }


    /**
     * GET /api/workflows - List all workflows
     * 
     * Thin Controller Pattern: Just delegates to service
     */
    @GetMapping
    public List<Workflow> getAllWorkflows() {
        return workflowService.getAllWorkflows();
    }

    /**
     * GET /api/workflows/{id} - Get workflow by ID
     * 
     * Thin Controller Pattern: HTTP response handling only,
     * business logic (finding) is in the service
     */
    @GetMapping("/{id}")
    public ResponseEntity<Workflow> getWorkflowById(@PathVariable Long id) {
        return workflowService.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    /**
     * POST /api/workflows - Create a new workflow
     * 
     * Thin Controller Pattern: Service handles all business logic
     * (ID assignment, status, timestamp). Controller only handles HTTP.
     */
    @PostMapping
    public ResponseEntity<Workflow> createWorkflow(@RequestBody Workflow workflow) {
        Workflow created = workflowService.createWorkflow(workflow);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }
}
