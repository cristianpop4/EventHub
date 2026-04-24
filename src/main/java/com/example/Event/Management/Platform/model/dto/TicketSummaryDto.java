package com.example.Event.Management.Platform.model.dto;

import com.example.Event.Management.Platform.model.enums.TicketType;

public record TicketSummaryDto(
        Long ticketId,
        TicketType type,
        Double price
) { }
