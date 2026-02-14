package com.nit.arwms.workflow;

import java.time.LocalDateTime;

/**
 * DTO for workflow API responses.
 *
 * Key Concept: Why a separate response DTO?
 * -------------------------------------------
 * 1. Controls exactly what data the client sees
 * 2. Prevents leaking internal fields (e.g., password hashes, DB internals)
 * 3. Allows formatting or renaming fields without touching the entity
 * 4. Can combine data from multiple entities into one response
 *
 * Using Java Record (immutable, concise):
 * - Automatically generates constructor, getters, equals, hashCode, toString
 * - Records are ideal for DTOs because they are read-only data carriers
 */
public record WorkflowResponse(
        Long id,
        String title,
        String description,
        String status,
        LocalDateTime createdAt) {

    /**
     * Factory method to convert a Workflow entity to a response DTO.
     *
     * Key Concept: Entity → DTO Conversion
     * This keeps the conversion logic in one place.
     */
    public static WorkflowResponse fromEntity(Workflow workflow) {
        return new WorkflowResponse(
                workflow.getId(),
                workflow.getTitle(),
                workflow.getDescription(),
                workflow.getStatus(),
                workflow.getCreatedAt());
    }
}
