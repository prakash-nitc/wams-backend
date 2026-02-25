package com.nit.arwms.exception;

import java.time.LocalDateTime;
import java.util.stream.Collectors;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import jakarta.servlet.http.HttpServletRequest;

/**
 * Global exception handler for the entire application.
 *
 * Phase 7 Update: Added handler for InvalidTransitionException (409 Conflict)
 * and HttpMessageNotReadableException (400 for invalid enum values).
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
         * Handles InvalidTransitionException → HTTP 409 Conflict
         *
         * 409 Conflict means: the request conflicts with the current state
         * of the resource. Perfect for invalid state transitions.
         */
        @ExceptionHandler(InvalidTransitionException.class)
        public ResponseEntity<ErrorResponse> handleInvalidTransition(
                        InvalidTransitionException ex, HttpServletRequest request) {

                ErrorResponse error = new ErrorResponse(
                                LocalDateTime.now(),
                                HttpStatus.CONFLICT.value(),
                                "Conflict",
                                ex.getMessage(),
                                request.getRequestURI());
                return ResponseEntity.status(HttpStatus.CONFLICT).body(error);
        }

        /**
         * Handles validation errors (@Valid failures) → HTTP 400 Bad Request
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
         * Handles invalid JSON / enum values → HTTP 400 Bad Request
         *
         * For example, sending "targetStatus": "INVALID_STATUS" would trigger this.
         */
        @ExceptionHandler(HttpMessageNotReadableException.class)
        public ResponseEntity<ErrorResponse> handleBadRequest(
                        HttpMessageNotReadableException ex, HttpServletRequest request) {

                ErrorResponse error = new ErrorResponse(
                                LocalDateTime.now(),
                                HttpStatus.BAD_REQUEST.value(),
                                "Bad Request",
                                "Invalid request body. Please check the JSON format and enum values.",
                                request.getRequestURI());
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
        }

        /**
         * Catch-all for any unhandled exceptions → HTTP 500 Internal Server Error
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
