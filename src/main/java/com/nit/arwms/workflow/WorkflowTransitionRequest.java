package com.nit.arwms.workflow;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

/**
 * DTO for requesting a workflow state transition.
 *
 * Key Concept: Role-Based Actions
 * ---------------------------------
 * The client must specify:
 * - targetStatus: the desired new status (e.g., SUBMITTED, APPROVED)
 * - role: the role of the user performing the action (e.g., REQUESTER,
 * APPROVER)
 *
 * The service validates that:
 * 1. The transition is valid (e.g., DRAFT → SUBMITTED is ok, DRAFT → APPROVED
 * is not)
 * 2. The role is authorized for this transition
 */
public class WorkflowTransitionRequest {

    @NotNull(message = "Target status is required")
    private WorkflowStatus targetStatus;

    @NotBlank(message = "Role is required")
    private String role;

    public WorkflowTransitionRequest() {
    }

    public WorkflowTransitionRequest(WorkflowStatus targetStatus, String role) {
        this.targetStatus = targetStatus;
        this.role = role;
    }

    public WorkflowStatus getTargetStatus() {
        return targetStatus;
    }

    public void setTargetStatus(WorkflowStatus targetStatus) {
        this.targetStatus = targetStatus;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }
}
