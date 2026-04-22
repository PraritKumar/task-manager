package com.prarit.taskmanager.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * Task entity — maps directly to the "tasks" table in PostgreSQL.
 *
 * Annotations explained:
 *   @Entity          : tells JPA this class is a database table
 *   @Table           : lets us name the table explicitly
 *   @Id              : marks the primary key field
 *   @GeneratedValue  : auto-increments the ID (Postgres SERIAL)
 *   @Column          : customise column constraints (nullable, length, etc.)
 *   @Data            : Lombok — generates getters, setters, equals, hashCode, toString
 *   @Builder         : Lombok — enables the builder pattern: Task.builder().title("x").build()
 *   @NoArgsConstructor / @AllArgsConstructor : Lombok — required by JPA + builder
 */
@Entity
@Table(name = "tasks")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Task {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Title must not be blank")
    @Column(nullable = false, length = 255)
    private String title;

    @Column(columnDefinition = "TEXT")
    private String description;

    /**
     * Task status — uses an enum stored as a String in the DB.
     * Possible values: TODO, IN_PROGRESS, DONE
     */
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @Builder.Default
    private TaskStatus status = TaskStatus.TODO;

    /**
     * Priority level for the task.
     * Possible values: LOW, MEDIUM, HIGH
     */
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @Builder.Default
    private TaskPriority priority = TaskPriority.MEDIUM;

    // Timestamps — set automatically, never manually
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(nullable = false)
    private LocalDateTime updatedAt;

    /**
     * @PrePersist runs automatically BEFORE a new Task is saved.
     * Sets both timestamps on creation.
     */
    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    /**
     * @PreUpdate runs automatically BEFORE an existing Task is updated.
     * Only updates the updatedAt timestamp.
     */
    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }

    // ── Enums ────────────────────────────────────────────────────────────────

    public enum TaskStatus {
        TODO,
        IN_PROGRESS,
        DONE
    }

    public enum TaskPriority {
        LOW,
        MEDIUM,
        HIGH
    }
}
