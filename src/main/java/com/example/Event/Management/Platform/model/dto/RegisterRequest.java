package com.example.Event.Management.Platform.model.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

public record RegisterRequest(
        @NotBlank(message = "Username is ")
        String username,

        @Email(message = "Invalid email")
        @NotBlank(message = " ")
        String email,

        @NotBlank(message = " ")
        @Pattern(
                regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]+$",
                message = "Parola trebuie sa contina litera mare, mica, cifra si caracter special"
        )
        String password
) { }
