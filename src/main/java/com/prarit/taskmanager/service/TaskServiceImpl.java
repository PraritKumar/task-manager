package com.prarit.taskmanager.service;

import com.prarit.taskmanager.dto.TaskRequestDTO;
import com.prarit.taskmanager.dto.TaskResponseDTO;
import com.prarit.taskmanager.exception.TaskNotFoundException;
import com.prarit.taskmanager.model.Task;
import com.prarit.taskmanager.model.Task.TaskPriority;
import com.prarit.taskmanager.model.Task.TaskStatus;
import com.prarit.taskmanager.repository.TaskRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * THE BRAIN OF THE APPLICATION.
 *
 * All business logic lives here — never in the Controller (that's just routing)
 * and never in the Repository (that's just data access).
 *
 * Key annotations:
 *
 * @Service         → Tells Spring this is a service bean. Spring creates ONE
 *                    instance and reuses it everywhere (singleton by default).
 *
 * @RequiredArgsConstructor → Lombok generates a constructor for all `final` fields.
 *                    This is constructor injection — the preferred way to inject
 *                    dependencies in Spring (better than @Autowired on fields).
 *
 * @Slf4j           → Lombok gives us a `log` variable for free.
 *                    Use: log.info(), log.warn(), log.error()
 *
 * @Transactional   → Wraps methods in a DB transaction. If anything fails,
 *                    the entire operation rolls back. Critical for data integrity.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class TaskServiceImpl implements TaskService {

    // Constructor injection — Spring automatically provides this
    private final TaskRepository taskRepository;

    // ── CREATE ────────────────────────────────────────────────────────────────

    @Override
    @Transactional
    public TaskResponseDTO createTask(TaskRequestDTO request) {
        log.info("Creating new task with title: {}", request.getTitle());

        // Build a Task entity from the incoming DTO
        Task task = Task.builder()
                .title(request.getTitle())
                .description(request.getDescription())
                // If client didn't send status/priority, use sensible defaults
                .status(request.getStatus() != null ? request.getStatus() : TaskStatus.TODO)
                .priority(request.getPriority() != null ? request.getPriority() : TaskPriority.MEDIUM)
                .build();

        // save() triggers @PrePersist → sets createdAt and updatedAt automatically
        Task saved = taskRepository.save(task);
        log.info("Task created successfully with id: {}", saved.getId());

        return TaskResponseDTO.fromEntity(saved);
    }

    // ── READ ──────────────────────────────────────────────────────────────────

    @Override
    @Transactional(readOnly = true)  // readOnly = true: DB optimisation for SELECT queries
    public TaskResponseDTO getTaskById(Long id) {
        log.info("Fetching task with id: {}", id);

        Task task = taskRepository.findById(id)
                // orElseThrow: if not found, throw our custom exception
                // This triggers the GlobalExceptionHandler to return a 404 response
                .orElseThrow(() -> new TaskNotFoundException("Task not found with id: " + id));

        return TaskResponseDTO.fromEntity(task);
    }

    @Override
    @Transactional(readOnly = true)
    public List<TaskResponseDTO> getAllTasks() {
        log.info("Fetching all tasks");

        return taskRepository.findAll()
                .stream()                                   // convert List to Stream
                .map(TaskResponseDTO::fromEntity)           // convert each Task → DTO
                .collect(Collectors.toList());              // collect back to List
    }

    @Override
    @Transactional(readOnly = true)
    public List<TaskResponseDTO> getTasksByStatus(TaskStatus status) {
        return taskRepository.findByStatus(status)
                .stream()
                .map(TaskResponseDTO::fromEntity)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<TaskResponseDTO> getTasksByPriority(TaskPriority priority) {
        return taskRepository.findByPriority(priority)
                .stream()
                .map(TaskResponseDTO::fromEntity)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<TaskResponseDTO> searchTasks(String keyword) {
        log.info("Searching tasks with keyword: {}", keyword);
        return taskRepository.findByTitleContainingIgnoreCase(keyword)
                .stream()
                .map(TaskResponseDTO::fromEntity)
                .collect(Collectors.toList());
    }

    // ── UPDATE ────────────────────────────────────────────────────────────────

    @Override
    @Transactional
    public TaskResponseDTO updateTask(Long id, TaskRequestDTO request) {
        log.info("Updating task with id: {}", id);

        Task task = taskRepository.findById(id)
                .orElseThrow(() -> new TaskNotFoundException("Task not found with id: " + id));

        // Only update fields that were actually sent by the client
        if (request.getTitle() != null)       task.setTitle(request.getTitle());
        if (request.getDescription() != null) task.setDescription(request.getDescription());
        if (request.getStatus() != null)      task.setStatus(request.getStatus());
        if (request.getPriority() != null)    task.setPriority(request.getPriority());

        // save() on an existing entity triggers UPDATE + @PreUpdate → sets updatedAt
        Task updated = taskRepository.save(task);
        return TaskResponseDTO.fromEntity(updated);
    }

    @Override
    @Transactional
    public TaskResponseDTO updateTaskStatus(Long id, TaskStatus status) {
        log.info("Updating status of task {} to {}", id, status);

        Task task = taskRepository.findById(id)
                .orElseThrow(() -> new TaskNotFoundException("Task not found with id: " + id));

        task.setStatus(status);
        return TaskResponseDTO.fromEntity(taskRepository.save(task));
    }

    // ── DELETE ────────────────────────────────────────────────────────────────

    @Override
    @Transactional
    public void deleteTask(Long id) {
        log.info("Deleting task with id: {}", id);

        // Check it exists before trying to delete — gives a proper 404 if not
        if (!taskRepository.existsById(id)) {
            throw new TaskNotFoundException("Task not found with id: " + id);
        }

        taskRepository.deleteById(id);
        log.info("Task {} deleted successfully", id);
    }
}
