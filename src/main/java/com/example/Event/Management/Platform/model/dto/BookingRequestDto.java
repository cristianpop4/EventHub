package com.example.Event.Management.Platform.model.dto;

public record BookingRequestDto(
        Long userId,
        Long eventId,
        Long ticketId
) { }
