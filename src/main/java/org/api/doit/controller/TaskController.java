package org.api.doit.controller;

import jakarta.validation.Valid;
import org.api.doit.dto.CreateTaskRequest;
import org.api.doit.dto.TaskResponse;
import org.api.doit.service.TaskService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

/**
 * Controller responsible for handling CRUD operations related to tasks.
 */
@RestController
@RequestMapping("/api/v1/tasks")
public class TaskController {
    private final TaskService taskService;

    public TaskController(TaskService taskService) {
        this.taskService = taskService;
    }

    /**
     * Creates a new task.
     *
     * @param createTaskRequest the request body containing task details
     * @return the created task
     */
    @PostMapping
    public ResponseEntity<?> createTask(@Valid @RequestBody CreateTaskRequest createTaskRequest) {
        return ResponseEntity.status(HttpStatus.CREATED).body(taskService.createTask(createTaskRequest));
    }

    /**
     * Retrieves all tasks or filters them by completion status if provided.
     *
     * @param completed optional filter to get only completed or uncompleted tasks
     * @return a list of tasks
     */
    @GetMapping
    public ResponseEntity<?> getTasks(@RequestParam(required = false) Boolean completed) {
        // Chooses between all tasks or filtering by completion status
        List<TaskResponse> tasks = completed == null ? taskService.getAllTasks() : taskService.getTasksByCompleted(completed);
        return ResponseEntity.ok().body(tasks);
    }

    /**
     * Retrieves a task by its UUID.
     *
     * @param id the unique identifier of the task
     * @return the corresponding task
     */
    @GetMapping("/{id}")
    public ResponseEntity<?> getTask(@PathVariable UUID id) {
        return ResponseEntity.ok().body(taskService.getTask(id));
    }

    /**
     * Toggles the 'completed' status of a task.
     *
     * @param id the task identifier
     * @return the updated task
     */
    @PatchMapping("/{id}/completed")
    public ResponseEntity<?> toggleTaskCompleted(@PathVariable UUID id) {
        return ResponseEntity.ok().body(taskService.toggleTaskCompleted(id));
    }

    /**
     * Deletes a task by its ID.
     *
     * @param id the unique identifier of the task
     * @return HTTP 204 if successful
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteTask(@PathVariable UUID id) {
        taskService.deleteTask(id);
        return ResponseEntity.noContent().build();
    }
}
