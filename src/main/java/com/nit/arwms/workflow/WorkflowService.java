package com.nit.arwms.workflow;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.stereotype.Service;

import com.nit.arwms.exception.WorkflowNotFoundException;

/**
 * Service layer for Workflow business logic.
 *
 * Phase 6 Update: findById now throws WorkflowNotFoundException
 * instead of returning Optional. This makes the service API cleaner
 * and lets the global exception handler manage error responses.
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
     */
    public WorkflowResponse createWorkflow(WorkflowRequest request) {
        Workflow workflow = new Workflow();
        workflow.setTitle(request.getTitle());
        workflow.setDescription(request.getDescription());
        workflow.setStatus("DRAFT");
        workflow.setCreatedAt(LocalDateTime.now());

        Workflow saved = workflowRepository.save(workflow);
        return WorkflowResponse.fromEntity(saved);
    }
}
