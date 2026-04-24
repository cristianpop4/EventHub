package com.example.Event.Management.Platform.model.dto;

import com.example.Event.Management.Platform.model.enums.BookingStatus;
import com.fasterxml.jackson.annotation.JsonFormat;

import java.time.LocalDateTime;

public record BookingResponseDto(
        Long bookingId,
        String username,
        EventSummaryDto event,
        TicketSummaryDto ticket,
        BookingStatus status,
        @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
        LocalDateTime registeredAt
) { }
