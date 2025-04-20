package org.api.doit.dto;

import java.util.UUID;

public record TaskResponse(UUID id, String title, String description, boolean completed) {
}
