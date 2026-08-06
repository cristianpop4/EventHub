package com.example.Event.Management.Platform.model.dto;

import com.example.Event.Management.Platform.model.enums.EventCategory;
import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

import java.time.LocalDateTime;

public record EventUpdateDto(
        @Size(min = 3,max = 100)
        String name,

        @Size(max = 2000)
        String description,

        EventCategory eventCategory,

        @Valid
        LocationRequestDto location,

        @Future
        @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
        LocalDateTime date,

        @Positive
        Integer maxParticipants
) { }
