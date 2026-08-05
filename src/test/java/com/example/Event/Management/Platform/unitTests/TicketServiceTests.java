package com.example.Event.Management.Platform.unitTests;

import com.example.Event.Management.Platform.model.dto.TicketRequestDto;
import com.example.Event.Management.Platform.model.dto.TicketResponseDto;
import com.example.Event.Management.Platform.model.dto.TicketUpdateDto;
import com.example.Event.Management.Platform.model.entity.Event;
import com.example.Event.Management.Platform.model.entity.Location;
import com.example.Event.Management.Platform.model.entity.Ticket;
import com.example.Event.Management.Platform.model.enums.EventCategory;
import com.example.Event.Management.Platform.model.enums.TicketType;
import com.example.Event.Management.Platform.model.exceptions.EventExceptions;
import com.example.Event.Management.Platform.model.exceptions.TicketExceptions;
import com.example.Event.Management.Platform.repository.EventRepository;
import com.example.Event.Management.Platform.repository.TicketRepository;
import com.example.Event.Management.Platform.service.serviceImpl.TicketServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DataAccessResourceFailureException;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class TicketServiceTests {

    @Mock
    private TicketRepository ticketRepository;

    @Mock
    private EventRepository eventRepository;

    @InjectMocks
    private TicketServiceImpl ticketService;

    private Ticket ticket;
    private Event event;
    private TicketRequestDto ticketRequest;
    private TicketUpdateDto ticketUpdate;

    @BeforeEach
    void setUp() {
        ticketRequest = new TicketRequestDto(
                1L,
                TicketType.STANDARD,
                99.9,
                200
        );

        ticket = new Ticket(
                1L,
                ticketRequest.type(),
                ticketRequest.price(),
                ticketRequest.availableQuantity(),
                null
        );

        event = new Event(
                1L,
                "Festival",
                "New festival",
                EventCategory.MUSIC,
                new Location(
                        1L,
                        null,
                        "Calea Bucuresti",
                        205,
                        "Tg Jiu",
                        "123432"
                ),
                LocalDateTime.of(2026, 6, 15, 12, 0, 0),
                200,
                null,
                List.of(ticket),
                null
        );

        ticketUpdate = new TicketUpdateDto(
                1L,
                TicketType.STANDARD,
                109.9,
                200
        );

        ticket.setEvent(event);
    }

    @Test
    void createTicket_ShouldCreateTicket_WhenValid() {
        when(eventRepository.findById(1L))
                .thenReturn(Optional.of(event));

        when(ticketRepository.getNumberOfTicketsAlreadyAllocated(ticketRequest.eventId()))
                .thenReturn(0);

        when(ticketRepository.save(any(Ticket.class)))
                .thenReturn(ticket);

        TicketResponseDto response = ticketService.createTicket(ticketRequest);

        assertEquals(TicketType.STANDARD, response.type());
        assertEquals(99.9, response.price());
        assertEquals(200, response.availableQuantity());

        verify(ticketRepository).save(argThat(saved ->
                saved.getType() == ticketRequest.type() &&
                        saved.getPrice() == ticketRequest.price() &&
                        saved.getAvailableQuantity() == ticketRequest.availableQuantity() &&
                        saved.getEvent().equals(event)
        ));
    }

    @Test
    void createTicket_ShouldThrow_WhenEventNotFound() {
        TicketRequestDto requestWithMissingEvent = new TicketRequestDto(
                99L, TicketType.STANDARD, 99.9, 200
        );

        when(eventRepository.findById(99L))
                .thenReturn(Optional.empty());

        assertThrows(EventExceptions.NotFoundExceptions.class,
                () -> ticketService.createTicket(requestWithMissingEvent));

        verifyNoInteractions(ticketRepository);
    }

    @Test
    void createTicket_ShouldThrow_WhenPriceNegative() {
        when(eventRepository.findById(1L))
                .thenReturn(Optional.of(event));

        TicketRequestDto requestWithNegativePrice = new TicketRequestDto(
                1L, TicketType.STANDARD, -1.0, 200
        );

        assertThrows(TicketExceptions.InvalidDataException.class,
                () -> ticketService.createTicket(requestWithNegativePrice));

        verifyNoInteractions(ticketRepository);
    }

    @Test
    void createTicket_ShouldThrow_WhenQuantityNotPositive() {
        when(eventRepository.findById(1L))
                .thenReturn(Optional.of(event));

        TicketRequestDto requestWithNegativeQuantity = new TicketRequestDto(
                1L, TicketType.STANDARD, 99.9, -1
        );

        assertThrows(TicketExceptions.InvalidDataException.class,
                () -> ticketService.createTicket(requestWithNegativeQuantity));

        verifyNoInteractions(ticketRepository);
    }

    @Test
    void createTicket_ShouldThrow_WhenAllocationCheckFails() {
        when(eventRepository.findById(1L))
                .thenReturn(Optional.of(event));

        when(ticketRepository.getNumberOfTicketsAlreadyAllocated(1L))
                .thenThrow(new DataAccessResourceFailureException("DB unavailable"));

        assertThrows(TicketExceptions.DataAccessException.class,
                () -> ticketService.createTicket(ticketRequest));

        verify(ticketRepository, never()).save(any());
    }

    @Test
    void createTicket_ShouldThrow_WhenCapacityExceeded() {
        when(eventRepository.findById(1L))
                .thenReturn(Optional.of(event));

        when(ticketRepository.getNumberOfTicketsAlreadyAllocated(1L))
                .thenReturn(1);

        assertThrows(TicketExceptions.CapacityExceededException.class,
                () -> ticketService.createTicket(ticketRequest));

        verify(ticketRepository, never()).save(any());
    }

    @Test
    void getTicketsByEventId_ShouldReturnListOfTickets() {
        when(eventRepository.findById(1L))
                .thenReturn(Optional.of(event));

        when(ticketRepository.findByEventId(1L))
                .thenReturn(List.of(ticket));

        List<TicketResponseDto> result = ticketService.getTicketsByEventId(1L);

        assertNotNull(result);
        assertEquals(1, result.size());

        verify(ticketRepository).findByEventId(1L);
    }

    @Test
    void getTicketsByEventId_ShouldThrow_WhenEventNotFound() {
        when(eventRepository.findById(99L))
                .thenReturn(Optional.empty());

        assertThrows(EventExceptions.NotFoundExceptions.class,
                () -> ticketService.getTicketsByEventId(99L));

        verify(ticketRepository, never()).findByEventId(99L);
    }

    @Test
    void getTicketById_ShouldReturnTicket() {
        when(ticketRepository.findById(1L))
                .thenReturn(Optional.of(ticket));

        TicketResponseDto result = ticketService.getTicketById(1L);

        assertNotNull(result);
        assertEquals(1L, result.ticketId());

        verify(ticketRepository).findById(1L);
    }

    @Test
    void getTicketById_ShouldThrow_WhenTicketNotFound() {
        when(ticketRepository.findById(99L))
                .thenReturn(Optional.empty());

        assertThrows(TicketExceptions.NotFoundException.class,
                () -> ticketService.getTicketById(99L));
    }

    @Test
    void updateTicket_ShouldUpdateTicket_WhenValid() {
        when(ticketRepository.findById(1L))
                .thenReturn(Optional.of(ticket));

        when(eventRepository.findById(1L))
                .thenReturn(Optional.of(event));

        when(ticketRepository.getNumberOfTicketsAlreadyAllocated(1L))
                .thenReturn(200);

        when(ticketRepository.save(any(Ticket.class)))
                .thenReturn(ticket);

        TicketResponseDto response = ticketService.updateTicket(1L, ticketUpdate);

        assertEquals(TicketType.STANDARD, response.type());
        assertEquals(109.9, response.price());
        assertEquals(200, response.availableQuantity());

        verify(ticketRepository).save(argThat(saved ->
                saved.getType() == ticketUpdate.type() &&
                        saved.getPrice() == ticketUpdate.price() &&
                        saved.getAvailableQuantity() == ticketUpdate.availableQuantity() &&
                        saved.getEvent().equals(event)
        ));
    }

    @Test
    void updateTicket_ShouldThrow_WhenTicketNotFound() {
        when(ticketRepository.findById(99L))
                .thenReturn(Optional.empty());

        assertThrows(TicketExceptions.NotFoundException.class,
                () -> ticketService.updateTicket(99L, ticketUpdate));

        verifyNoInteractions(eventRepository);
        verify(ticketRepository, never()).save(any());
    }

    @Test
    void updateTicket_ShouldThrow_WhenEventNotFound() {
        TicketUpdateDto updateWithMissingEvent = new TicketUpdateDto(
                99L, TicketType.STANDARD, 99.9, 200
        );

        when(ticketRepository.findById(1L))
                .thenReturn(Optional.of(ticket));

        when(eventRepository.findById(99L))
                .thenReturn(Optional.empty());

        assertThrows(EventExceptions.NotFoundExceptions.class,
                () -> ticketService.updateTicket(1L, updateWithMissingEvent));

        verify(ticketRepository, never()).save(any());
    }

    @Test
    void updateTicket_ShouldThrow_WhenPriceNegative() {
        when(ticketRepository.findById(1L))
                .thenReturn(Optional.of(ticket));

        when(eventRepository.findById(1L))
                .thenReturn(Optional.of(event));

        TicketUpdateDto updateWithNegativePrice = new TicketUpdateDto(
                1L, TicketType.STANDARD, -1.0, 200
        );

        assertThrows(TicketExceptions.InvalidDataException.class,
                () -> ticketService.updateTicket(1L, updateWithNegativePrice));

        verify(ticketRepository, never()).save(any());
    }

    @Test
    void updateTicket_ShouldThrow_WhenQuantityNotPositive() {
        when(ticketRepository.findById(1L))
                .thenReturn(Optional.of(ticket));

        when(eventRepository.findById(1L))
                .thenReturn(Optional.of(event));

        TicketUpdateDto updateWithNegativeQuantity = new TicketUpdateDto(
                1L, TicketType.STANDARD, 99.9, -1
        );

        assertThrows(TicketExceptions.InvalidDataException.class,
                () -> ticketService.updateTicket(1L, updateWithNegativeQuantity));

        verify(ticketRepository, never()).save(any());
    }

    @Test
    void updateTicket_ShouldThrow_WhenAllocationCheckFails() {
        when(ticketRepository.findById(1L))
                .thenReturn(Optional.of(ticket));

        when(eventRepository.findById(1L))
                .thenReturn(Optional.of(event));

        when(ticketRepository.getNumberOfTicketsAlreadyAllocated(1L))
                .thenThrow(new DataAccessResourceFailureException("DB unavailable"));

        assertThrows(TicketExceptions.DataAccessException.class,
                () -> ticketService.updateTicket(1L, ticketUpdate));

        verify(ticketRepository, never()).save(any());
    }

    @Test
    void updateTicket_ShouldThrow_WhenCapacityExceeded() {
        when(ticketRepository.findById(1L))
                .thenReturn(Optional.of(ticket));

        when(eventRepository.findById(1L))
                .thenReturn(Optional.of(event));

        TicketUpdateDto updateOverCapacity = new TicketUpdateDto(
                1L, TicketType.STANDARD, 99.9, 250
        );

        when(ticketRepository.getNumberOfTicketsAlreadyAllocated(1L))
                .thenReturn(200);

        assertThrows(TicketExceptions.CapacityExceededException.class,
                () -> ticketService.updateTicket(1L, updateOverCapacity));

        verify(ticketRepository, never()).save(any());
    }

    @Test
    void deleteTicketById_ShouldDeleteTicket() {
        when(ticketRepository.findById(1L))
                .thenReturn(Optional.of(ticket));

        ticketService.deleteTicketById(1L);

        verify(ticketRepository).delete(any());
    }

    @Test
    void deleteTicket_ShouldThrow_WhenTicketNotFound() {
        when(ticketRepository.findById(99L))
                .thenReturn(Optional.empty());

        assertThrows(TicketExceptions.NotFoundException.class,
                () -> ticketService.deleteTicketById(99L));

        verify(ticketRepository, never()).deleteById(any());
    }

    @Test
    void getTicketTypes_ShouldReturn_ListOfTicketTypes() {
        List<String> result = ticketService.getTicketTypes();

        assertNotNull(result);
        assertEquals(TicketType.values().length, result.size());
        assertTrue(result.containsAll(
                Arrays.stream(TicketType.values())
                        .map(Enum::name)
                        .toList()
        ));
    }

    @Test
    void checkAvailability_ShouldReturnTrue_WhenTicketsAreAvailable() {
        when(ticketRepository.findById(1L))
                .thenReturn(Optional.of(ticket));

        boolean result = ticketService.checkAvailability(1L);

        assertTrue(result);

        verify(ticketRepository).findById(1L);
    }

    @Test
    void checkAvailability_ShouldReturnFalse_WhenTicketsAreUnavailable() {
        when(ticketRepository.findById(1L))
                .thenReturn(Optional.of(ticket));

        ticket.setAvailableQuantity(0);

        boolean result = ticketService.checkAvailability(1L);

        assertFalse(result);

        verify(ticketRepository).findById(1L);
    }

    @Test
    void checkAvailability_ShouldThrow_WhenTicketNotFound() {
        when(ticketRepository.findById(99L))
                .thenReturn(Optional.empty());

        assertThrows(TicketExceptions.NotFoundException.class,
                () -> ticketService.checkAvailability(99L));

        verify(ticketRepository).findById(99L);
    }

    @Test
    void decreaseAvailability__ShouldDecreaseQuantityByOne_WhenTicketsAreAvailable() {
        when(ticketRepository.findByIdWithLock(1L))
                .thenReturn(Optional.of(ticket));

        ticketService.decreaseAvailability(1L);

        assertEquals(199, ticket.getAvailableQuantity());

        verify(ticketRepository).findByIdWithLock(1L);
        verify(ticketRepository).save(ticket);
    }

    @Test
    void decreaseAvailability_ShouldThrowNotAvailableException_WhenNoTicketsAreAvailable() {
        ticket.setAvailableQuantity(0);

        when(ticketRepository.findByIdWithLock(1L))
                .thenReturn(Optional.of(ticket));

        assertThrows(
                TicketExceptions.NotAvailableException.class,
                () -> ticketService.decreaseAvailability(1L)
        );

        verify(ticketRepository).findByIdWithLock(1L);
        verify(ticketRepository, never()).save(any(Ticket.class));
    }

    @Test
    void decreaseAvailability_ShouldThrowNotFoundException_WhenTicketDoesNotExist() {
        when(ticketRepository.findByIdWithLock(1L))
                .thenReturn(Optional.empty());

        assertThrows(
                TicketExceptions.NotFoundException.class,
                () -> ticketService.decreaseAvailability(1L)
        );

        verify(ticketRepository).findByIdWithLock(1L);
        verify(ticketRepository, never()).save(any(Ticket.class));
    }

    @Test
    void increaseAvailability_ShouldIncreaseQuantityByOne_WhenTicketExists() {
        ticket.setAvailableQuantity(10);

        when(ticketRepository.findById(1L))
                .thenReturn(Optional.of(ticket));

        ticketService.increaseAvailability(1L);

        assertEquals(11, ticket.getAvailableQuantity());

        verify(ticketRepository).findById(1L);
        verify(ticketRepository).save(ticket);
    }

    @Test
    void increaseAvailability_ShouldThrowNotFoundException_WhenTicketDoesNotExist() {
        when(ticketRepository.findById(1L))
                .thenReturn(Optional.empty());
        assertThrows(
                TicketExceptions.NotFoundException.class,
                () -> ticketService.increaseAvailability(1L)
        );

        verify(ticketRepository).findById(1L);
        verify(ticketRepository, never()).save(any(Ticket.class));
    }
}
