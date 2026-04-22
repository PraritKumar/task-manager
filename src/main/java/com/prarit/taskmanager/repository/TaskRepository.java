package com.prarit.taskmanager.repository;

import com.prarit.taskmanager.model.Task;
import com.prarit.taskmanager.model.Task.TaskPriority;
import com.prarit.taskmanager.model.Task.TaskStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * WHAT IS A REPOSITORY?
 *
 * Think of it as your database remote control.
 * By extending JpaRepository<Task, Long>, Spring auto-generates
 * ALL basic database operations for free — no SQL needed:
 *
 *   save(task)           → INSERT or UPDATE
 *   findById(id)         → SELECT WHERE id = ?
 *   findAll()            → SELECT * FROM tasks
 *   deleteById(id)       → DELETE WHERE id = ?
 *   existsById(id)       → SELECT COUNT WHERE id = ?
 *   count()              → SELECT COUNT(*) FROM tasks
 *
 * The two parameters: JpaRepository<Task, Long>
 *   Task → the entity this repo manages
 *   Long → the type of the primary key (our @Id field)
 *
 * CUSTOM QUERIES:
 * Spring also auto-generates queries from method names.
 * You don't write SQL — Spring reads the method name and figures it out.
 * Example: findByStatus → SELECT * FROM tasks WHERE status = ?
 */
@Repository
public interface TaskRepository extends JpaRepository<Task, Long> {

    // Spring generates: SELECT * FROM tasks WHERE status = ?
    List<Task> findByStatus(TaskStatus status);

    // Spring generates: SELECT * FROM tasks WHERE priority = ?
    List<Task> findByPriority(TaskPriority priority);

    // Spring generates: SELECT * FROM tasks WHERE status = ? AND priority = ?
    List<Task> findByStatusAndPriority(TaskStatus status, TaskPriority priority);

    // Spring generates: SELECT * FROM tasks WHERE LOWER(title) LIKE LOWER('%keyword%')
    List<Task> findByTitleContainingIgnoreCase(String keyword);
}
