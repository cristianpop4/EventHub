package com.example.Event.Management.Platform.model.dto;

import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record UserUpdateDto(
        @Size(min = 2, max = 50, message = "The name must have between 2 and 50 characters")
        String username,

        @Pattern(
                regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]+$",
                message = "The password must contain uppercase, lowercase, number and special character"
        )
        String password
) {
}
