package com.example.Event.Management.Platform.model.dto;

import com.example.Event.Management.Platform.model.enums.EventCategory;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;

public record EventSearchDto(
        String name,
        String city,
        EventCategory eventCategory,
        @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
        LocalDateTime dateTime
) { }
