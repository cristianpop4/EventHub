package com.example.Event.Management.Platform.model.dto;

import jakarta.validation.constraints.*;

public record LocationRequestDto(
        @NotBlank
        @Size(max=100)
        String streetName,

        @NotNull
        @Positive
        Integer number,

        @NotBlank
        @Size(max=100)
        String city,

        @NotBlank
        @Pattern(
                regexp="^[0-9A-Za-z -]{4,12}$",
                message="Invalid zip code"
        )
        String zipCode
) { }
