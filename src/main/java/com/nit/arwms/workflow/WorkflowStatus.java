package com.nit.arwms.workflow;

import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Enum defining all possible workflow states and their valid transitions.
 *
 * Key Concept: State Machine with Enums
 * ----------------------------------------
 * Each enum constant defines:
 * 1. Which states it can transition TO
 * 2. Which role is REQUIRED to perform the transition
 *
 * State flow:
 * DRAFT ──(submit)──→ SUBMITTED ──(review)──→ UNDER_REVIEW
 * │
 * ┌─────────┴─────────┐
 * (approve) (reject)
 * ↓ ↓
 * APPROVED REJECTED
 *
 * Role requirements:
 * - REQUESTER can: submit (DRAFT → SUBMITTED)
 * - REVIEWER can: review (SUBMITTED → UNDER_REVIEW)
 * - APPROVER can: approve (UNDER_REVIEW → APPROVED)
 * reject (UNDER_REVIEW → REJECTED)
 */
public enum WorkflowStatus {

    DRAFT,
    SUBMITTED,
    UNDER_REVIEW,
    APPROVED,
    REJECTED;

    /**
     * Defines valid transitions: from-status → set of allowed to-statuses.
     */
    private static final Map<WorkflowStatus, Set<WorkflowStatus>> VALID_TRANSITIONS = Map.of(
            DRAFT, Set.of(SUBMITTED),
            SUBMITTED, Set.of(UNDER_REVIEW),
            UNDER_REVIEW, Set.of(APPROVED, REJECTED));

    /**
     * Defines which role is required for each transition.
     * Key format: "FROM→TO"
     */
    private static final Map<String, String> REQUIRED_ROLES = Map.of(
            "DRAFT→SUBMITTED", "REQUESTER",
            "SUBMITTED→UNDER_REVIEW", "REVIEWER",
            "UNDER_REVIEW→APPROVED", "APPROVER",
            "UNDER_REVIEW→REJECTED", "APPROVER");

    /**
     * Checks if transitioning to the target status is valid.
     */
    public boolean canTransitionTo(WorkflowStatus target) {
        Set<WorkflowStatus> allowed = VALID_TRANSITIONS.get(this);
        return allowed != null && allowed.contains(target);
    }

    /**
     * Returns the role required to perform the transition from this status to
     * target.
     *
     * @return the required role name, or null if transition is invalid
     */
    public String requiredRoleFor(WorkflowStatus target) {
        return REQUIRED_ROLES.get(this.name() + "→" + target.name());
    }

    /**
     * Returns the list of statuses this status can transition to.
     */
    public List<WorkflowStatus> allowedTransitions() {
        Set<WorkflowStatus> transitions = VALID_TRANSITIONS.get(this);
        return transitions != null ? List.copyOf(transitions) : List.of();
    }
}
