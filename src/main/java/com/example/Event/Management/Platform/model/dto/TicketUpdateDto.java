package com.example.Event.Management.Platform.model.dto;

import com.example.Event.Management.Platform.model.enums.TicketType;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;

public record TicketUpdateDto(
        Long eventId,

        TicketType type,

        @PositiveOrZero
        Double price,

        @Positive
        Integer availableQuantity
) { }
