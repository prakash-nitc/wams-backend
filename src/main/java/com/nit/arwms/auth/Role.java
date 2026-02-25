package com.nit.arwms.auth;

/**
 * Enum defining user roles in the system.
 *
 * Key Concept: Role-Based Access Control (RBAC)
 * -----------------------------------------------
 * Each user has exactly one role that determines what actions they can perform:
 *
 * - REQUESTER: Can create workflows and submit them for review
 * - REVIEWER: Can take submitted workflows into review
 * - APPROVER: Can approve or reject workflows under review
 *
 * These roles map directly to the WorkflowStatus transition rules defined
 * in WorkflowStatus.java (Phase 7).
 */
public enum Role {
    REQUESTER,
    REVIEWER,
    APPROVER
}
