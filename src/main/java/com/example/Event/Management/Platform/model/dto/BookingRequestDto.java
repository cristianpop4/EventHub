package com.example.Event.Management.Platform.model.dto;

import jakarta.validation.constraints.Positive;
import org.jetbrains.annotations.NotNull;

public record BookingRequestDto(
        @NotNull
        @Positive
        Long eventId,

        @NotNull
        @Positive
        Long ticketId
) { }
