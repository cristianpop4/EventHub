package com.example.Event.Management.Platform.model.dto;

import com.example.Event.Management.Platform.model.enums.EventCategory;
import com.fasterxml.jackson.annotation.JsonFormat;

import java.time.LocalDateTime;

public record EventUpdateDto(
        String name,
        String description,
        EventCategory eventCategory,
        LocationRequestDto location,
        @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
        LocalDateTime date,
        Integer maxParticipants,
        Long organizerId
) { }
