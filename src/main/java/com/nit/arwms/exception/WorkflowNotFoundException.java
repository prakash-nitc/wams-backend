package com.nit.arwms.exception;

/**
 * Custom exception thrown when a requested workflow is not found.
 *
 * Key Concept: Custom Exceptions
 * --------------------------------
 * - Extends RuntimeException (unchecked — no forced try/catch)
 * - Carries a meaningful message describing what went wrong
 * - Will be caught by GlobalExceptionHandler and converted to HTTP 404
 *
 * Why not just return null or Optional.empty()?
 * - Exceptions make the error case EXPLICIT in the code flow
 * - The global handler ensures a consistent error response format
 * - Service methods can focus on the happy path
 */
public class WorkflowNotFoundException extends RuntimeException {

    public WorkflowNotFoundException(Long id) {
        super("Workflow not found with id: " + id);
    }
}
