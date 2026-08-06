package com.example.Event.Management.Platform.model.dto;

import com.example.Event.Management.Platform.model.enums.EventCategory;
import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;


import java.time.LocalDateTime;

public record EventRequestDto(
        @NotBlank(message = "Event name is required")
        @Size(min = 3,max = 100)
        String name,

        @NotBlank(message = "Description is required")
        @Size(max = 2000)
        String description,

        @NotNull(message = "Category is required")
        EventCategory eventCategory,

        @Valid
        @NotNull(message = "Location is required")
        LocationRequestDto location,

        @NotNull(message = "Date is required")
        @Future(message = "Date must be in the future")
        @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
        LocalDateTime date,

        @NotNull(message = "Maximum participants is required")
        @Positive(message = "Participants must be positive")
        Integer maxParticipants
) { }
