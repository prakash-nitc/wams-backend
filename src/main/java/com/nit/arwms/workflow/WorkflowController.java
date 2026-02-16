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

import jakarta.validation.Valid;

/**
 * REST Controller for Workflow operations.
 *
 * Phase 6 Update: Controller no longer handles errors directly.
 * - findById throws WorkflowNotFoundException (caught by
 * GlobalExceptionHandler)
 * - Validation errors caught by GlobalExceptionHandler
 * - Controller stays thin — no try/catch or error logic here
 */
@RestController
@RequestMapping("/api/workflows")
public class WorkflowController {

    private final WorkflowService workflowService;

    public WorkflowController(WorkflowService workflowService) {
        this.workflowService = workflowService;
    }

    /**
     * GET /api/workflows - List all workflows
     */
    @GetMapping
    public List<WorkflowResponse> getAllWorkflows() {
        return workflowService.getAllWorkflows();
    }

    /**
     * GET /api/workflows/{id} - Get workflow by ID
     *
     * No error handling here — if not found, WorkflowNotFoundException
     * is thrown and GlobalExceptionHandler returns a proper 404 response.
     */
    @GetMapping("/{id}")
    public WorkflowResponse getWorkflowById(@PathVariable Long id) {
        return workflowService.findById(id);
    }

    /**
     * POST /api/workflows - Create a new workflow
     *
     * @Valid triggers validation. On failure, MethodArgumentNotValidException
     *        is thrown and GlobalExceptionHandler returns a proper 400 response.
     */
    @PostMapping
    public ResponseEntity<WorkflowResponse> createWorkflow(@Valid @RequestBody WorkflowRequest request) {
        WorkflowResponse created = workflowService.createWorkflow(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }
}
