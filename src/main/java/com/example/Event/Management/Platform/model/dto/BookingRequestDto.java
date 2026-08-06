package com.example.Event.Management.Platform.model.dto;

import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.NotNull;

public record BookingRequestDto(
        @NotNull
        @Positive
        Long eventId,

        @NotNull
        @Positive
        Long ticketId
) { }
