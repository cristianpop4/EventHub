package com.example.Event.Management.Platform.model.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record RegisterRequest(
        @NotBlank(message = "Username is required")
        @Size(min = 2, max = 50, message = "The name must have between 2 and 50 characters")
        String username,

        @NotBlank(message = "Email is required")
        @Email(message = "Invalid email")
        String email,

        @NotBlank(message = " ")
        @Pattern(
                regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]+$",
                message = "The password must contain uppercase, lowercase, number and special character"
        )
        String password
) { }
