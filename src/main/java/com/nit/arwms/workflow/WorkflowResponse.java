package com.nit.arwms.workflow;

import java.time.LocalDateTime;

/**
 * DTO for workflow API responses.
 *
 * Phase 7 Update: Status is now a String representation of WorkflowStatus enum.
 * The API still returns a string (e.g., "DRAFT"), keeping the contract simple.
 */
public record WorkflowResponse(
        Long id,
        String title,
        String description,
        String status,
        LocalDateTime createdAt) {

    /**
     * Factory method to convert a Workflow entity to a response DTO.
     * Converts the enum status to its string name for the API.
     */
    public static WorkflowResponse fromEntity(Workflow workflow) {
        return new WorkflowResponse(
                workflow.getId(),
                workflow.getTitle(),
                workflow.getDescription(),
                workflow.getStatus().name(),
                workflow.getCreatedAt());
    }
}
