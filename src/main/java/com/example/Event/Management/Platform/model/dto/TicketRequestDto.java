package com.example.Event.Management.Platform.model.dto;

import com.example.Event.Management.Platform.model.enums.TicketType;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;

public record TicketRequestDto(
        @NotNull
        Long eventId,

        @NotNull
        TicketType type,

        @NotNull
        @PositiveOrZero
        Double price,

        @NotNull
        @Positive
        Integer availableQuantity
) { }
