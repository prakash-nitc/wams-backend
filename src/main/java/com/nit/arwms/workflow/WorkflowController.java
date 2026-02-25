package com.nit.arwms.workflow;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.nit.arwms.auth.User;

import jakarta.validation.Valid;

/**
 * REST Controller for Workflow operations.
 *
 * Phase 8 Update: Transition endpoint now extracts the role from
 * the authenticated user's JWT token instead of trusting the request body.
 * The "role" field in WorkflowTransitionRequest is still used for backward
 * compatibility but the authenticated user's role takes priority.
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
     */
    @GetMapping("/{id}")
    public WorkflowResponse getWorkflowById(@PathVariable Long id) {
        return workflowService.findById(id);
    }

    /**
     * POST /api/workflows - Create a new workflow
     */
    @PostMapping
    public ResponseEntity<WorkflowResponse> createWorkflow(@Valid @RequestBody WorkflowRequest request) {
        WorkflowResponse created = workflowService.createWorkflow(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    /**
     * PATCH /api/workflows/{id}/transition - Transition workflow status
     *
     * Phase 8 Update: Uses the authenticated user's role from the JWT token.
     * The role in the request body is overridden with the actual user's role.
     */
    @PatchMapping("/{id}/transition")
    public WorkflowResponse transitionWorkflow(
            @PathVariable Long id,
            @Valid @RequestBody WorkflowTransitionRequest request,
            Authentication authentication) {

        // Extract the authenticated user's role from the security context
        if (authentication != null && authentication.getPrincipal() instanceof User user) {
            request.setRole(user.getRole().name());
        }

        return workflowService.transitionWorkflow(id, request);
    }
}
