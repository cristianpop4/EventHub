package com.example.Event.Management.Platform.service.serviceImpl;

import com.example.Event.Management.Platform.model.dto.TicketRequestDto;
import com.example.Event.Management.Platform.model.dto.TicketResponseDto;
import com.example.Event.Management.Platform.model.dto.TicketUpdateDto;
import com.example.Event.Management.Platform.model.entity.Event;
import com.example.Event.Management.Platform.model.entity.Ticket;
import com.example.Event.Management.Platform.model.enums.TicketType;
import com.example.Event.Management.Platform.model.exceptions.EventExceptions;
import com.example.Event.Management.Platform.model.exceptions.TicketExceptions;
import com.example.Event.Management.Platform.repository.EventRepository;
import com.example.Event.Management.Platform.repository.TicketRepository;
import com.example.Event.Management.Platform.service.TicketServiceForBooking;
import com.example.Event.Management.Platform.service.TicketServiceForController;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;

@Service
@RequiredArgsConstructor
public class TicketServiceImpl implements TicketServiceForController, TicketServiceForBooking {
    private final TicketRepository ticketRepository;
    private final EventRepository eventRepository;

    @Override
    public TicketResponseDto createTicket(TicketRequestDto dto) {
        Event event = eventRepository.findById(dto.eventId())
                .orElseThrow(() -> new EventExceptions.NotFoundExceptions(dto.eventId()));

        if (dto.price() < 0) {
            throw new TicketExceptions.InvalidDataException("price", "must be positive");
        }

        if (dto.availableQuantity() <= 0) {
            throw new TicketExceptions.InvalidDataException("available quantity", "must be greater than 0");
        }

        Integer alreadyAllocated;

        try {
            alreadyAllocated = ticketRepository.getNumberOfTicketsAlreadyAllocated(dto.eventId());
        } catch (DataAccessException e) {
            throw new TicketExceptions.DataAccessException(dto.eventId());
        }

        if (alreadyAllocated + dto.availableQuantity() > event.getMaxParticipants()) {
            throw new TicketExceptions.CapacityExceededException(event.getMaxParticipants());
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
        eventRepository.findById(eventId)
                .orElseThrow(() -> new EventExceptions.NotFoundExceptions(eventId));
        return ticketRepository.findByEventId(eventId)
                .stream().map(this::toDto)
                .toList();
    }

    @Override
    public TicketResponseDto getTicketById(Long ticketId) {
        return toDto(ticketRepository.findById(ticketId)
                .orElseThrow(() -> new TicketExceptions.NotFoundException(ticketId)));
    }

    @Override
    public TicketResponseDto updateTicket(Long ticketId, TicketUpdateDto update) {
        Ticket ticket = ticketRepository.findById(ticketId)
                .orElseThrow(() -> new TicketExceptions.NotFoundException(ticketId));

        Event event = eventRepository.findById(update.eventId())
                .orElseThrow(() -> new EventExceptions.NotFoundExceptions(update.eventId()));

        if (update.price() < 0) {
            throw new TicketExceptions.InvalidDataException("price", "must be positive");
        }

        if (update.availableQuantity() <= 0) {
            throw new TicketExceptions.InvalidDataException("available quantity", "must be greater than 0");
        }

        Integer alreadyAllocated;

        try {
            alreadyAllocated = ticketRepository.getNumberOfTicketsAlreadyAllocated(update.eventId());
        } catch (DataAccessException e) {
            throw new TicketExceptions.DataAccessException(update.eventId());
        }

        if ((alreadyAllocated - ticket.getAvailableQuantity()) + update.availableQuantity() > event.getMaxParticipants()) {
            throw new TicketExceptions.CapacityExceededException(event.getMaxParticipants());
        }

        ticket.setEvent(event);
        ticket.setType(update.type());
        ticket.setPrice(update.price());
        ticket.setAvailableQuantity(update.availableQuantity());

        return toDto(ticketRepository.save(ticket));
    }

    @Override
    public void deleteTicketById(Long ticketId) {
        Ticket ticket = ticketRepository.findById(ticketId)
                .orElseThrow(() -> new TicketExceptions.NotFoundException(ticketId));

        ticketRepository.delete(ticket);
    }

    @Override
    public List<String> getTicketTypes() {
        return Arrays.stream(TicketType.values())
                .map(Enum::name)
                .toList();
    }

    @Override
    public boolean checkAvailability(Long ticketId) {
        Ticket ticket = ticketRepository.findById(ticketId)
                .orElseThrow(() -> new TicketExceptions.NotFoundException(ticketId));

        return ticket.getAvailableQuantity() >= 1;
    }

    @Override
    public void decreaseAvailability(Long ticketId) {
        Ticket ticket = ticketRepository.findById(ticketId)
                .orElseThrow(() -> new TicketExceptions.NotFoundException(ticketId));

        if (ticket.getAvailableQuantity() < 1) {
            throw new TicketExceptions.NotAvailableException(ticketId);
        }

        ticket.setAvailableQuantity(ticket.getAvailableQuantity() - 1);

        ticketRepository.save(ticket);
    }

    @Override
    public void increaseAvailability(Long ticketId) {
        Ticket ticket = ticketRepository.findById(ticketId)
                .orElseThrow(() -> new TicketExceptions.NotFoundException(ticketId));

        ticket.setAvailableQuantity(ticket.getAvailableQuantity() + 1);

        ticketRepository.save(ticket);
    }
}
