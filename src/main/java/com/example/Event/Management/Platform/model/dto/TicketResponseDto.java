package com.example.Event.Management.Platform.model.dto;

import com.example.Event.Management.Platform.model.enums.TicketType;

public record TicketResponseDto(
        Long ticketId,
        EventSummaryDto event,
        TicketType type,
        Double price,
        Integer availableQuantity
) { }
