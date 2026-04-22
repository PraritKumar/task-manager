package com.prarit.taskmanager.dto;

import com.prarit.taskmanager.model.Task;
import com.prarit.taskmanager.model.Task.TaskPriority;
import com.prarit.taskmanager.model.Task.TaskStatus;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * What we send BACK to the client after any operation.
 *
 * Notice it includes id, createdAt, updatedAt — fields the client
 * can't set but definitely wants to READ.
 *
 * The static factory method fromEntity() is a clean pattern to
 * convert a Task entity → TaskResponseDTO in one line anywhere in the code.
 */
@Data
@Builder
public class TaskResponseDTO {

    private Long id;
    private String title;
    private String description;
    private TaskStatus status;
    private TaskPriority priority;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    /**
     * Converts a Task entity into a TaskResponseDTO.
     * Usage:  TaskResponseDTO.fromEntity(task)
     */
    public static TaskResponseDTO fromEntity(Task task) {
        return TaskResponseDTO.builder()
                .id(task.getId())
                .title(task.getTitle())
                .description(task.getDescription())
                .status(task.getStatus())
                .priority(task.getPriority())
                .createdAt(task.getCreatedAt())
                .updatedAt(task.getUpdatedAt())
                .build();
    }
}
