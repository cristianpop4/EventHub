package com.example.Event.Management.Platform.service;

import com.example.Event.Management.Platform.model.dto.*;
import com.example.Event.Management.Platform.model.entity.Ticket;

import java.util.List;

public interface TicketServiceForController {
    TicketResponseDto createTicket(TicketRequestDto dto);
    List<TicketResponseDto> getTicketsByEventId(Long eventId);
    TicketResponseDto getTicketById(Long ticketId);
    TicketResponseDto updateTicket(Long ticketId, TicketUpdateDto update);
    void deleteTicketById(Long ticketId);

    default TicketResponseDto toDto(Ticket ticket){
        return new TicketResponseDto(
                ticket.getId(),
                new EventSummaryDto(
                        ticket.getEvent().getId(),
                        ticket.getEvent().getName(),
                        ticket.getEvent().getDate()
                ),
                ticket.getType(),
                ticket.getPrice(),
                ticket.getAvailableQuantity()
        );
    }
}
