package com.example.Event.Management.Platform.model.dto;

import com.example.Event.Management.Platform.model.enums.EventCategory;

import java.time.LocalDateTime;

public record EventUpdateDto(
        String name,
        String description,
        EventCategory eventCategory,
        LocationRequestDto location,
        LocalDateTime date,
        Integer maxParticipants,
        Long organizerId
) { }
