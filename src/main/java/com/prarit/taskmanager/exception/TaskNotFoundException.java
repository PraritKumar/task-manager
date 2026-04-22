package com.prarit.taskmanager.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Custom exception for when a task ID doesn't exist in the database.
 *
 * @ResponseStatus(HttpStatus.NOT_FOUND) tells Spring:
 * "When this exception is thrown, automatically return HTTP 404"
 *
 * WHY CUSTOM EXCEPTIONS?
 * Without this, Spring would return a generic 500 Internal Server Error
 * for any unhandled exception — which gives the client no useful info.
 *
 * With custom exceptions + GlobalExceptionHandler, we return structured,
 * meaningful error responses like:
 *   { "status": 404, "message": "Task not found with id: 99" }
 */
@ResponseStatus(HttpStatus.NOT_FOUND)
public class TaskNotFoundException extends RuntimeException {

    public TaskNotFoundException(String message) {
        super(message);
    }
}
