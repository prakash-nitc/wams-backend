package com.nit.arwms.workflow;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.stereotype.Service;

import com.nit.arwms.exception.InvalidTransitionException;
import com.nit.arwms.exception.WorkflowNotFoundException;

/**
 * Service layer for Workflow business logic.
 *
 * Phase 7 Update: Added transitionWorkflow() method implementing
 * the state machine with role-based authorization.
 */
@Service
public class WorkflowService {

    private final WorkflowRepository workflowRepository;

    public WorkflowService(WorkflowRepository workflowRepository) {
        this.workflowRepository = workflowRepository;
    }

    /**
     * Retrieves all workflows and converts them to response DTOs.
     */
    public List<WorkflowResponse> getAllWorkflows() {
        return workflowRepository.findAll().stream()
                .map(WorkflowResponse::fromEntity)
                .toList();
    }

    /**
     * Finds a workflow by ID.
     *
     * @throws WorkflowNotFoundException if no workflow exists with the given ID
     */
    public WorkflowResponse findById(Long id) {
        Workflow workflow = workflowRepository.findById(id)
                .orElseThrow(() -> new WorkflowNotFoundException(id));
        return WorkflowResponse.fromEntity(workflow);
    }

    /**
     * Creates a new workflow from a request DTO.
     * New workflows always start in DRAFT status.
     */
    public WorkflowResponse createWorkflow(WorkflowRequest request) {
        Workflow workflow = new Workflow();
        workflow.setTitle(request.getTitle());
        workflow.setDescription(request.getDescription());
        workflow.setStatus(WorkflowStatus.DRAFT);
        workflow.setCreatedAt(LocalDateTime.now());

        Workflow saved = workflowRepository.save(workflow);
        return WorkflowResponse.fromEntity(saved);
    }

    /**
     * Transitions a workflow to a new status.
     *
     * Validates:
     * 1. The workflow exists
     * 2. The transition is valid (checked against enum state machine)
     * 3. The role is authorized for this transition
     *
     * @throws WorkflowNotFoundException  if workflow doesn't exist
     * @throws InvalidTransitionException if transition is invalid or role is
     *                                    unauthorized
     */
    public WorkflowResponse transitionWorkflow(Long id, WorkflowTransitionRequest request) {
        Workflow workflow = workflowRepository.findById(id)
                .orElseThrow(() -> new WorkflowNotFoundException(id));

        WorkflowStatus currentStatus = workflow.getStatus();
        WorkflowStatus targetStatus = request.getTargetStatus();

        // Validate the transition is allowed
        if (!currentStatus.canTransitionTo(targetStatus)) {
            throw new InvalidTransitionException(
                    "Cannot transition from " + currentStatus + " to " + targetStatus
                            + ". Allowed transitions: " + currentStatus.allowedTransitions());
        }

        // Validate the role is authorized
        String requiredRole = currentStatus.requiredRoleFor(targetStatus);
        if (requiredRole != null && !requiredRole.equalsIgnoreCase(request.getRole())) {
            throw new InvalidTransitionException(
                    "Role '" + request.getRole() + "' is not authorized for this transition. "
                            + "Required role: " + requiredRole);
        }

        // Perform the transition
        workflow.setStatus(targetStatus);
        Workflow saved = workflowRepository.save(workflow);
        return WorkflowResponse.fromEntity(saved);
    }
}
