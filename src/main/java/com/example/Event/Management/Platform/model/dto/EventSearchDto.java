package com.example.Event.Management.Platform.model.dto;

import com.example.Event.Management.Platform.model.enums.EventCategory;

import java.time.LocalDateTime;

public record EventSearchDto(
        String city,
        EventCategory eventCategory,
        LocalDateTime dateTime
) { }
