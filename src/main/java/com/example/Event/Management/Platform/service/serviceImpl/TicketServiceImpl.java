package com.example.Event.Management.Platform.service.serviceImpl;

import com.example.Event.Management.Platform.model.dto.TicketRequestDto;
import com.example.Event.Management.Platform.model.dto.TicketResponseDto;
import com.example.Event.Management.Platform.model.dto.TicketUpdateDto;
import com.example.Event.Management.Platform.model.entity.Event;
import com.example.Event.Management.Platform.model.entity.Ticket;
import com.example.Event.Management.Platform.repository.EventRepository;
import com.example.Event.Management.Platform.repository.TicketRepository;
import com.example.Event.Management.Platform.service.TicketServiceForBooking;
import com.example.Event.Management.Platform.service.TicketServiceForController;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class TicketServiceImpl implements TicketServiceForController, TicketServiceForBooking {
    private final TicketRepository ticketRepository;
    private final EventRepository eventRepository;

    @Override
    public TicketResponseDto createTicket(TicketRequestDto dto) {
        Event event = eventRepository.findById(dto.eventId())
                .orElseThrow(()-> new RuntimeException("Event not found"));

        if (dto.price() < 0) {
            throw new RuntimeException("Price must be positive");
        }

        if (dto.availableQuantity() <= 0) {
            throw new RuntimeException("Quantity must be greater than 0");
        }

        Ticket ticket = new Ticket();
        ticket.setType(dto.type());
        ticket.setPrice(dto.price());
        ticket.setAvailableQuantity(dto.availableQuantity());
        ticket.setEvent(event);

        return toDto(ticketRepository.save(ticket));
    }

    @Override
    public List<TicketResponseDto> getTicketsByEventId(Long eventId) {
        return ticketRepository.findByEventId(eventId)
                .stream().map(this::toDto)
                .toList();
    }

    @Override
    public TicketResponseDto getTicketById(Long ticketId) {
        return toDto(ticketRepository.findById(ticketId)
                .orElseThrow(()-> new RuntimeException("Ticket not found")));
    }

    @Override
    public TicketResponseDto updateTicket(Long ticketId, TicketUpdateDto update) {
        Ticket ticket = ticketRepository.findById(ticketId)
                .orElseThrow(()-> new RuntimeException("Ticket not found"));

        if (update.price() < 0) {
            throw new RuntimeException("Price must be positive");
        }

        if (update.availableQuantity() <= 0) {
            throw new RuntimeException("Quantity must be greater than 0");
        }

        ticket.setEvent(eventRepository.findById(update.eventId())
                .orElseThrow(()->new RuntimeException("Event not found")));
        ticket.setType(update.type());
        ticket.setPrice(update.price());
        ticket.setAvailableQuantity(update.availableQuantity());

        return toDto(ticketRepository.save(ticket));
    }

    @Override
    public void deleteTicketById(Long ticketId) {
        Ticket ticket = ticketRepository.findById(ticketId)
                .orElseThrow(()-> new RuntimeException("Ticket not found"));

        ticketRepository.delete(ticket);
    }

    @Override
    public boolean checkAvailability(Long ticketId, Integer quantity) {
        Ticket ticket = ticketRepository.findById(ticketId)
                .orElseThrow(()-> new RuntimeException("Ticket not found"));

        if (quantity == null || quantity <= 0) {
            throw new RuntimeException("Quantity must be greater than 0");
        }

        return ticket.getAvailableQuantity() >= quantity;
    }

    @Override
    public void decreaseAvailability(Long ticketId, Integer quantity) {
        Ticket ticket = ticketRepository.findById(ticketId)
                .orElseThrow(()-> new RuntimeException("Ticket not found"));

        if (quantity == null || quantity <= 0) {
            throw new RuntimeException("Quantity must be greater than 0");
        }

        if (ticket.getAvailableQuantity() < quantity) {
            throw new RuntimeException("Not enough tickets available");
        }

        ticket.setAvailableQuantity(ticket.getAvailableQuantity() - quantity);

        ticketRepository.save(ticket);
    }
}
