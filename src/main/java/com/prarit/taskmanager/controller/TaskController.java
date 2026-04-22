package com.prarit.taskmanager.controller;

import com.prarit.taskmanager.dto.TaskRequestDTO;
import com.prarit.taskmanager.dto.TaskResponseDTO;
import com.prarit.taskmanager.model.Task.TaskPriority;
import com.prarit.taskmanager.model.Task.TaskStatus;
import com.prarit.taskmanager.service.TaskService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * THE FRONT DOOR OF THE API.
 *
 * The Controller's ONLY job: receive HTTP requests → call the service → return the response.
 * No business logic here. No database calls. Just routing.
 *
 * Key annotations:
 *
 * @RestController  → @Controller + @ResponseBody. Every method automatically
 *                    serialises the return value to JSON.
 *
 * @RequestMapping  → base URL prefix for all endpoints in this class.
 *                    All our endpoints start with /api/v1/tasks
 *
 * @Tag             → Swagger UI grouping label
 *
 * HTTP Methods explained (REST convention):
 *   GET    → Read data      (safe, no side effects)
 *   POST   → Create         (returns 201 Created)
 *   PUT    → Full update    (replace entire resource)
 *   PATCH  → Partial update (update one field only)
 *   DELETE → Delete         (returns 204 No Content)
 */
@RestController
@RequestMapping("/api/v1/tasks")
@RequiredArgsConstructor
@Tag(name = "Tasks", description = "Task Management API")
public class TaskController {

    private final TaskService taskService;

    // ── CREATE ────────────────────────────────────────────────────────────────

    /**
     * POST /api/v1/tasks
     * Creates a new task.
     *
     * @Valid triggers validation rules defined in TaskRequestDTO (@NotBlank etc.)
     * If validation fails → GlobalExceptionHandler returns 400 with field errors
     */
    @PostMapping
    @Operation(summary = "Create a new task")
    public ResponseEntity<TaskResponseDTO> createTask(@Valid @RequestBody TaskRequestDTO request) {
        TaskResponseDTO created = taskService.createTask(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);  // 201
    }

    // ── READ ──────────────────────────────────────────────────────────────────

    /**
     * GET /api/v1/tasks
     * Returns all tasks. Supports optional filtering via query params:
     *   /api/v1/tasks?status=TODO
     *   /api/v1/tasks?priority=HIGH
     *   /api/v1/tasks?search=meeting
     */
    @GetMapping
    @Operation(summary = "Get all tasks (with optional filters)")
    public ResponseEntity<List<TaskResponseDTO>> getAllTasks(
            @RequestParam(required = false) TaskStatus status,
            @RequestParam(required = false) TaskPriority priority,
            @RequestParam(required = false) String search) {

        List<TaskResponseDTO> tasks;

        if (search != null && !search.isBlank()) {
            tasks = taskService.searchTasks(search);
        } else if (status != null) {
            tasks = taskService.getTasksByStatus(status);
        } else if (priority != null) {
            tasks = taskService.getTasksByPriority(priority);
        } else {
            tasks = taskService.getAllTasks();
        }

        return ResponseEntity.ok(tasks);  // 200
    }

    /**
     * GET /api/v1/tasks/{id}
     * Returns a single task by its ID.
     * Returns 404 if not found (handled by GlobalExceptionHandler).
     */
    @GetMapping("/{id}")
    @Operation(summary = "Get a task by ID")
    public ResponseEntity<TaskResponseDTO> getTaskById(@PathVariable Long id) {
        return ResponseEntity.ok(taskService.getTaskById(id));
    }

    // ── UPDATE ────────────────────────────────────────────────────────────────

    /**
     * PUT /api/v1/tasks/{id}
     * Full update — replaces all fields with what the client sends.
     */
    @PutMapping("/{id}")
    @Operation(summary = "Update a task")
    public ResponseEntity<TaskResponseDTO> updateTask(
            @PathVariable Long id,
            @Valid @RequestBody TaskRequestDTO request) {
        return ResponseEntity.ok(taskService.updateTask(id, request));
    }

    /**
     * PATCH /api/v1/tasks/{id}/status
     * Partial update — only changes the status field.
     * Example: PATCH /api/v1/tasks/1/status?status=DONE
     *
     * PATCH is preferred over PUT when you're only changing one field —
     * less data sent over the wire, cleaner semantics.
     */
    @PatchMapping("/{id}/status")
    @Operation(summary = "Update only the status of a task")
    public ResponseEntity<TaskResponseDTO> updateTaskStatus(
            @PathVariable Long id,
            @RequestParam TaskStatus status) {
        return ResponseEntity.ok(taskService.updateTaskStatus(id, status));
    }

    // ── DELETE ────────────────────────────────────────────────────────────────

    /**
     * DELETE /api/v1/tasks/{id}
     * Deletes a task. Returns 204 No Content (standard for deletes — no body).
     */
    @DeleteMapping("/{id}")
    @Operation(summary = "Delete a task")
    public ResponseEntity<Void> deleteTask(@PathVariable Long id) {
        taskService.deleteTask(id);
        return ResponseEntity.noContent().build();  // 204
    }
}
