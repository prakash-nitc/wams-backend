package com.nit.arwms.workflow;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

/**
 * Service layer for Workflow business logic.
 *
 * Phase 5 Update: Service now accepts WorkflowRequest DTO
 * and returns WorkflowResponse DTO, keeping a clean boundary
 * between API contracts and internal entities.
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
     * Finds a workflow by ID and converts to response DTO.
     */
    public Optional<WorkflowResponse> findById(Long id) {
        return workflowRepository.findById(id)
                .map(WorkflowResponse::fromEntity);
    }

    /**
     * Creates a new workflow from a request DTO.
     *
     * Business Logic:
     * - Converts DTO to entity
     * - Sets initial status to DRAFT
     * - Records creation timestamp
     * - Persists and returns response DTO
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
