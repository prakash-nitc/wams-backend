package com.nit.arwms.exception;

import java.time.LocalDateTime;
import java.util.stream.Collectors;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import jakarta.servlet.http.HttpServletRequest;

/**
 * Global exception handler for the entire application.
 *
 * Key Concept: @RestControllerAdvice
 * ------------------------------------
 * - Intercepts exceptions thrown by ANY controller
 * - Maps each exception type to a proper HTTP status + error response
 * - Eliminates duplicate error-handling code across controllers
 * - Single place to control the error response format
 *
 * How it works:
 * 1. Controller method throws an exception
 * 2. Spring looks for a matching @ExceptionHandler method here
 * 3. The handler builds an ErrorResponse and returns it as JSON
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    /**
     * Handles WorkflowNotFoundException → HTTP 404 Not Found
     */
    @ExceptionHandler(WorkflowNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleWorkflowNotFound(
            WorkflowNotFoundException ex, HttpServletRequest request) {

        ErrorResponse error = new ErrorResponse(
                LocalDateTime.now(),
                HttpStatus.NOT_FOUND.value(),
                "Not Found",
                ex.getMessage(),
                request.getRequestURI());
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
    }

    /**
     * Handles validation errors (@Valid failures) → HTTP 400 Bad Request
     *
     * Extracts all field-level validation error messages and joins them
     * into a single readable message.
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidationErrors(
            MethodArgumentNotValidException ex, HttpServletRequest request) {

        String message = ex.getBindingResult().getFieldErrors().stream()
                .map(fieldError -> fieldError.getField() + ": " + fieldError.getDefaultMessage())
                .collect(Collectors.joining("; "));

        ErrorResponse error = new ErrorResponse(
                LocalDateTime.now(),
                HttpStatus.BAD_REQUEST.value(),
                "Bad Request",
                message,
                request.getRequestURI());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
    }

    /**
     * Catch-all for any unhandled exceptions → HTTP 500 Internal Server Error
     *
     * This prevents raw stack traces from being sent to the client.
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGenericException(
            Exception ex, HttpServletRequest request) {

        ErrorResponse error = new ErrorResponse(
                LocalDateTime.now(),
                HttpStatus.INTERNAL_SERVER_ERROR.value(),
                "Internal Server Error",
                "An unexpected error occurred",
                request.getRequestURI());
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
    }
}
