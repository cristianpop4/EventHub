package com.example.Event.Management.Platform.model.dto;

import com.example.Event.Management.Platform.model.enums.EventCategory;
import jakarta.validation.constraints.Size;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;

public record EventSearchDto(
        @Size(max=100)
        String name,

        @Size(max=100)
        String city,

        EventCategory eventCategory,

        @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
        LocalDateTime dateTime
) { }
