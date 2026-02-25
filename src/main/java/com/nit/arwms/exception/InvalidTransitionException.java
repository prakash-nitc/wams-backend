package com.nit.arwms.exception;

/**
 * Thrown when a workflow state transition is not allowed.
 *
 * Examples:
 * - Trying to approve a DRAFT workflow (must be UNDER_REVIEW)
 * - A REQUESTER trying to approve (only APPROVER can)
 * - Trying to transition an already APPROVED workflow
 */
public class InvalidTransitionException extends RuntimeException {

    public InvalidTransitionException(String message) {
        super(message);
    }
}
