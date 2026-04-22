package com.prarit.taskmanager.service;

import com.prarit.taskmanager.dto.TaskRequestDTO;
import com.prarit.taskmanager.dto.TaskResponseDTO;
import com.prarit.taskmanager.exception.TaskNotFoundException;
import com.prarit.taskmanager.model.Task;
import com.prarit.taskmanager.repository.TaskRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * UNIT TESTS FOR TaskServiceImpl.
 *
 * WHAT IS A UNIT TEST?
 * Tests a single class IN ISOLATION — no real database, no real HTTP calls.
 * We "mock" all dependencies so we test ONLY the service logic.
 *
 * KEY ANNOTATIONS:
 *
 * @ExtendWith(MockitoExtension.class)
 *   → Enables Mockito in JUnit 5. Must be present.
 *
 * @Mock
 *   → Creates a fake (mock) TaskRepository. No real DB connection.
 *     We control exactly what it returns in each test.
 *
 * @InjectMocks
 *   → Creates a REAL TaskServiceImpl, but injects the mock repository into it.
 *     So we test real service logic with a fake repository.
 *
 * HOW MOCKITO WORKS:
 *   when(repo.findById(1L)).thenReturn(Optional.of(task))
 *   → "When findById(1) is called, pretend to return this task"
 *
 *   verify(repo, times(1)).save(any())
 *   → "Assert that save() was called exactly once"
 */
@ExtendWith(MockitoExtension.class)
class TaskServiceTest {

    @Mock
    private TaskRepository taskRepository;

    @InjectMocks
    private TaskServiceImpl taskService;

    private Task sampleTask;
    private TaskRequestDTO sampleRequest;

    @BeforeEach  // Runs before EACH test — sets up fresh test data
    void setUp() {
        sampleTask = Task.builder()
                .id(1L)
                .title("Write unit tests")
                .description("Cover service layer")
                .status(Task.TaskStatus.TODO)
                .priority(Task.TaskPriority.HIGH)
                .build();

        // Manually set timestamps (normally set by @PrePersist)
        sampleTask.setCreatedAt(LocalDateTime.now());
        sampleTask.setUpdatedAt(LocalDateTime.now());

        sampleRequest = new TaskRequestDTO();
        sampleRequest.setTitle("Write unit tests");
        sampleRequest.setDescription("Cover service layer");
        sampleRequest.setPriority(Task.TaskPriority.HIGH);
    }

    // ── CREATE TESTS ──────────────────────────────────────────────────────────

    @Test
    @DisplayName("Should create task and return response DTO")
    void createTask_Success() {
        // ARRANGE: tell the mock what to return when save() is called
        when(taskRepository.save(any(Task.class))).thenReturn(sampleTask);

        // ACT: call the real service method
        TaskResponseDTO result = taskService.createTask(sampleRequest);

        // ASSERT: check the result is what we expect
        assertThat(result).isNotNull();
        assertThat(result.getTitle()).isEqualTo("Write unit tests");
        assertThat(result.getStatus()).isEqualTo(Task.TaskStatus.TODO);  // default
        assertThat(result.getPriority()).isEqualTo(Task.TaskPriority.HIGH);

        // Verify save() was called exactly once
        verify(taskRepository, times(1)).save(any(Task.class));
    }

    // ── READ TESTS ────────────────────────────────────────────────────────────

    @Test
    @DisplayName("Should return task when valid ID is provided")
    void getTaskById_WhenExists_ReturnsTask() {
        when(taskRepository.findById(1L)).thenReturn(Optional.of(sampleTask));

        TaskResponseDTO result = taskService.getTaskById(1L);

        assertThat(result.getId()).isEqualTo(1L);
        assertThat(result.getTitle()).isEqualTo("Write unit tests");
    }

    @Test
    @DisplayName("Should throw TaskNotFoundException when ID does not exist")
    void getTaskById_WhenNotFound_ThrowsException() {
        when(taskRepository.findById(99L)).thenReturn(Optional.empty());

        // assertThatThrownBy: asserts that calling this code throws the expected exception
        assertThatThrownBy(() -> taskService.getTaskById(99L))
                .isInstanceOf(TaskNotFoundException.class)
                .hasMessageContaining("99");
    }

    @Test
    @DisplayName("Should return all tasks as DTOs")
    void getAllTasks_ReturnsListOfDTOs() {
        when(taskRepository.findAll()).thenReturn(List.of(sampleTask));

        List<TaskResponseDTO> result = taskService.getAllTasks();

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getTitle()).isEqualTo("Write unit tests");
    }

    // ── DELETE TESTS ──────────────────────────────────────────────────────────

    @Test
    @DisplayName("Should delete task when it exists")
    void deleteTask_WhenExists_Succeeds() {
        when(taskRepository.existsById(1L)).thenReturn(true);

        taskService.deleteTask(1L);

        // Verify deleteById was actually called with the right ID
        verify(taskRepository, times(1)).deleteById(1L);
    }

    @Test
    @DisplayName("Should throw exception when deleting non-existent task")
    void deleteTask_WhenNotFound_ThrowsException() {
        when(taskRepository.existsById(99L)).thenReturn(false);

        assertThatThrownBy(() -> taskService.deleteTask(99L))
                .isInstanceOf(TaskNotFoundException.class);

        // Verify deleteById was NEVER called (we stopped early)
        verify(taskRepository, never()).deleteById(any());
    }
}
