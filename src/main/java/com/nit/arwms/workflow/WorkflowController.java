package com.nit.arwms.workflow;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;

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
 */
@RestController
@RequestMapping("/api/workflows")
public class WorkflowController {

    // In-memory storage for workflows (will be replaced by database in Phase 4)
    private final List<Workflow> workflows = new ArrayList<>();
    private final AtomicLong idCounter = new AtomicLong(1);

    /**
     * GET /api/workflows - List all workflows
     */
    @GetMapping
    public List<Workflow> getAllWorkflows() {
        return workflows;
    }

    /**
     * GET /api/workflows/{id} - Get workflow by ID
     */
    @GetMapping("/{id}")
    public ResponseEntity<Workflow> getWorkflowById(@PathVariable Long id) {
        return workflows.stream()
                .filter(w -> w.getId().equals(id))
                .findFirst()
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    /**
     * POST /api/workflows - Create a new workflow
     */
    @PostMapping
    public ResponseEntity<Workflow> createWorkflow(@RequestBody Workflow workflow) {
        workflow.setId(idCounter.getAndIncrement());
        workflow.setStatus("DRAFT");
        workflow.setCreatedAt(LocalDateTime.now());
        workflows.add(workflow);
        return ResponseEntity.status(HttpStatus.CREATED).body(workflow);
    }
}
