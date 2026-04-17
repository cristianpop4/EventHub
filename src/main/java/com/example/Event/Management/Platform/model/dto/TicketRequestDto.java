package com.example.Event.Management.Platform.model.dto;

import com.example.Event.Management.Platform.model.enums.TicketType;

public record TicketRequestDto(
        Long eventId,
        TicketType type,
        Double price,
        Integer availableQuantity
) { }
