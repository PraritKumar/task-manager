package com.prarit.taskmanager.dto;

import com.prarit.taskmanager.model.Task.TaskPriority;
import com.prarit.taskmanager.model.Task.TaskStatus;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

/**
 * DTO = Data Transfer Object.
 *
 * WHY DTOs EXIST:
 * We never expose our database entity (Task.java) directly to the outside world.
 * Reason: The entity has fields like createdAt, updatedAt that the client
 * should NOT be able to set — those are server-controlled.
 *
 * So we use two DTOs:
 *   TaskRequestDTO  → what the CLIENT sends TO us   (input)
 *   TaskResponseDTO → what WE send TO the client    (output)
 *
 * This is standard production practice — you'll see this in every serious codebase.
 */
@Data  // Lombok: generates getters, setters, equals, hashCode, toString
public class TaskRequestDTO {

    @NotBlank(message = "Title is required")
    @Size(min = 3, max = 255, message = "Title must be between 3 and 255 characters")
    private String title;

    @Size(max = 2000, message = "Description cannot exceed 2000 characters")
    private String description;

    // Optional fields — if client doesn't send them, we use defaults in the service
    private TaskStatus status;
    private TaskPriority priority;
}
