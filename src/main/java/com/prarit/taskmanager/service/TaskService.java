package com.prarit.taskmanager.service;

import com.prarit.taskmanager.dto.TaskRequestDTO;
import com.prarit.taskmanager.dto.TaskResponseDTO;
import com.prarit.taskmanager.model.Task.TaskPriority;
import com.prarit.taskmanager.model.Task.TaskStatus;

import java.util.List;

/**
 * WHY DO WE HAVE AN INTERFACE + IMPLEMENTATION?
 *
 * This is the SOLID principle in action — specifically the "D":
 * Dependency Inversion Principle.
 *
 * The Controller depends on THIS interface, not on TaskServiceImpl directly.
 * This means:
 *   1. We can swap the implementation without touching the Controller
 *   2. We can mock this interface easily in unit tests (Mockito)
 *   3. It documents exactly what the service CAN do — a clean contract
 *
 * Every professional Java codebase does this. Recruiters look for it.
 */
public interface TaskService {

    TaskResponseDTO createTask(TaskRequestDTO request);

    TaskResponseDTO getTaskById(Long id);

    List<TaskResponseDTO> getAllTasks();

    List<TaskResponseDTO> getTasksByStatus(TaskStatus status);

    List<TaskResponseDTO> getTasksByPriority(TaskPriority priority);

    List<TaskResponseDTO> searchTasks(String keyword);

    TaskResponseDTO updateTask(Long id, TaskRequestDTO request);

    TaskResponseDTO updateTaskStatus(Long id, TaskStatus status);

    void deleteTask(Long id);
}
