package com.nit.arwms.workflow;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * DTO for creating a new workflow (request body).
 *
 * Key Concept: DTO Pattern
 * -------------------------
 * DTOs separate API contracts from internal entities. This ensures:
 * 1. Clients can't set fields they shouldn't (e.g., id, status, createdAt)
 * 2. Entity changes don't break the API
 * 3. Validation rules are clear and explicit
 *
 * Key Concept: Validation Annotations
 * ------------------------------------
 * 
 * @NotBlank — Must not be null and must contain at least one non-whitespace
 *           character
 * @Size — Constrains string length
 */
public class WorkflowRequest {

    @NotBlank(message = "Title is required")
    @Size(max = 100, message = "Title must be at most 100 characters")
    private String title;

    @Size(max = 500, message = "Description must be at most 500 characters")
    private String description;

    // Default constructor (needed for JSON deserialization)
    public WorkflowRequest() {
    }

    public WorkflowRequest(String title, String description) {
        this.title = title;
        this.description = description;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
