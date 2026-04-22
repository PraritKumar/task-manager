package com.prarit.taskmanager.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

/**
 * GLOBAL EXCEPTION HANDLER — catches exceptions from ALL controllers.
 *
 * @RestControllerAdvice = @ControllerAdvice + @ResponseBody
 * It intercepts exceptions thrown anywhere in the app and converts
 * them into clean, consistent JSON error responses.
 *
 * Without this, Spring returns ugly HTML error pages or
 * stack traces — completely unusable for an API client.
 *
 * This is a MUST-HAVE in every production Spring Boot API.
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    /**
     * Handles TaskNotFoundException → 404 Not Found
     * Triggered when a task ID doesn't exist.
     */
    @ExceptionHandler(TaskNotFoundException.class)
    public ResponseEntity<Map<String, Object>> handleTaskNotFound(TaskNotFoundException ex) {
        return buildErrorResponse(HttpStatus.NOT_FOUND, ex.getMessage());
    }

    /**
     * Handles validation errors → 400 Bad Request
     * Triggered when @Valid fails on a request body
     * e.g. title is blank, description is too long, etc.
     *
     * Returns field-level errors like:
     * { "title": "Title is required", "description": "Cannot exceed 2000 chars" }
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, Object>> handleValidationErrors(MethodArgumentNotValidException ex) {
        Map<String, String> fieldErrors = new HashMap<>();

        // Loop through each failed validation and collect field → error message
        for (FieldError error : ex.getBindingResult().getFieldErrors()) {
            fieldErrors.put(error.getField(), error.getDefaultMessage());
        }

        Map<String, Object> body = new HashMap<>();
        body.put("timestamp", LocalDateTime.now());
        body.put("status", HttpStatus.BAD_REQUEST.value());
        body.put("error", "Validation Failed");
        body.put("fieldErrors", fieldErrors);

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(body);
    }

    /**
     * Catch-all handler → 500 Internal Server Error
     * Catches any unexpected exception we didn't specifically handle.
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, Object>> handleGenericException(Exception ex) {
        return buildErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR,
                "An unexpected error occurred: " + ex.getMessage());
    }

    // ── Helper ───────────────────────────────────────────────────────────────

    private ResponseEntity<Map<String, Object>> buildErrorResponse(HttpStatus status, String message) {
        Map<String, Object> body = new HashMap<>();
        body.put("timestamp", LocalDateTime.now());
        body.put("status", status.value());
        body.put("error", status.getReasonPhrase());
        body.put("message", message);
        return ResponseEntity.status(status).body(body);
    }
}
