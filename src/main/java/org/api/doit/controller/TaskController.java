package org.api.doit.controller;

import org.api.doit.dto.CreateTaskRequest;
import org.api.doit.service.TaskService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/tasks/")
public class TaskController {
    private final TaskService taskService;

    public TaskController(TaskService taskService) {
        this.taskService = taskService;
    }

    @PostMapping
    public ResponseEntity<?> createTask(@RequestBody CreateTaskRequest createTaskRequest) {
        return ResponseEntity.ok().body(taskService.createTask(createTaskRequest));
    }

    @GetMapping
    public ResponseEntity<?> getTasks() {
        return ResponseEntity.ok().body(taskService.getTasks());
    }

    @PatchMapping("/{id}/completed")
    public ResponseEntity<?> toggleTaskCompleted(@PathVariable UUID id) {
        return ResponseEntity.ok().body(taskService.markTaskAsCompleted(id));
    }
}
