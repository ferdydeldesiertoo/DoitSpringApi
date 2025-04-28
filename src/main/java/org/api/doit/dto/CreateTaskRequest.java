package org.api.doit.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record CreateTaskRequest(
        @NotBlank(message = "The task title cannot be empty")
        @Size(min = 3, max = 30, message = "The task title must contain between 3 to 30 characters.")
        String title,

        @Size(max = 250, message = "The task description allows up to 255 characters only.")
        String description
) {

    public CreateTaskRequest {
        title = title != null ? title.trim() : null;
        description = description != null ? description.trim() : null;
    }
}
