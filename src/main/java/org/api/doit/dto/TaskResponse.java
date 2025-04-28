package org.api.doit.dto;

import org.api.doit.entity.Task;

import java.time.LocalDateTime;
import java.util.UUID;

public record TaskResponse(UUID id, String title, String description, boolean completed, LocalDateTime createdAt) {
    public static TaskResponse from(Task task) {
        return new TaskResponse(
                task.getId(),
                task.getTitle(),
                task.getDescription(),
                task.isCompleted(),
                task.getCreatedAt());
    }
}
