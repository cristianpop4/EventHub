package com.example.Event.Management.Platform.model.dto;

import com.example.Event.Management.Platform.model.enums.EventCategory;

import java.time.LocalDateTime;

public record EventResponseDto(
    Long id,
    String name,
    String description,
    EventCategory eventCategory,
    LocationResponseDto location,
    LocalDateTime date,
    Integer maxParticipants,
    String organizerName
) { }
