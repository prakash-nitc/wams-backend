package com.nit.arwms.exception;

import java.time.LocalDateTime;

/**
 * Standardized error response body for all API errors.
 *
 * Key Concept: Consistent Error Responses
 * -----------------------------------------
 * Every error response from the API follows the same shape:
 * {
 * "timestamp": "2026-02-16T11:00:00",
 * "status": 404,
 * "error": "Not Found",
 * "message": "Workflow not found with id: 42",
 * "path": "/api/workflows/42"
 * }
 *
 * This makes it easy for frontend developers and API consumers
 * to write a single error-handling function.
 */
public record ErrorResponse(
        LocalDateTime timestamp,
        int status,
        String error,
        String message,
        String path) {
}
