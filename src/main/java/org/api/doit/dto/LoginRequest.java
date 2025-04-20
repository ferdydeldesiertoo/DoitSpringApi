package org.api.doit.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record LoginRequest(@NotBlank(message = "Username cannot be empty.")
                           @Size(min = 3, max = 20, message = "The username must contain between 3 to 20 characters.")
                           String username,

                           @NotBlank(message = "Password cannot be empty.")
                           @Size(min = 8, max = 20, message = "The password must contain between 8 to 20 characters.")
                           String password) {
}
