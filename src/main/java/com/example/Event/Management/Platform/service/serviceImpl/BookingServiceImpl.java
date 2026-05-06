package com.example.Event.Management.Platform.service.serviceImpl;

import com.example.Event.Management.Platform.model.dto.BookingRequestDto;
import com.example.Event.Management.Platform.model.dto.BookingResponseDto;
import com.example.Event.Management.Platform.model.entity.Booking;
import com.example.Event.Management.Platform.model.entity.Event;
import com.example.Event.Management.Platform.model.entity.Ticket;
import com.example.Event.Management.Platform.model.entity.User;
import com.example.Event.Management.Platform.model.enums.BookingStatus;
import com.example.Event.Management.Platform.model.exceptions.BookingExceptions;
import com.example.Event.Management.Platform.model.exceptions.EventExceptions;
import com.example.Event.Management.Platform.model.exceptions.TicketExceptions;
import com.example.Event.Management.Platform.model.exceptions.UserExceptions;
import com.example.Event.Management.Platform.repository.BookingRepository;
import com.example.Event.Management.Platform.repository.EventRepository;
import com.example.Event.Management.Platform.repository.TicketRepository;
import com.example.Event.Management.Platform.repository.UserRepository;
import com.example.Event.Management.Platform.service.BookingService;
import com.example.Event.Management.Platform.service.TicketServiceForBooking;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class BookingServiceImpl implements BookingService {
    private final BookingRepository bookingRepository;
    private final EventRepository eventRepository;
    private final UserRepository userRepository;
    private final TicketRepository ticketRepository;
    private final TicketServiceForBooking ticketService;

    @Override
    public BookingResponseDto createBooking(BookingRequestDto requestDto) {
        User user = userRepository.findById(requestDto.userId())
                .orElseThrow(() -> new UserExceptions.NotFoundException(requestDto.userId()));

        Event event = eventRepository.findById(requestDto.eventId())
                .orElseThrow(() -> new EventExceptions.NotFoundExceptions(requestDto.eventId()));

        Ticket ticket = ticketRepository.findById(requestDto.ticketId())
                .orElseThrow(() -> new TicketExceptions.NotFoundException(requestDto.ticketId()));

        if (!ticketService.checkAvailability(requestDto.ticketId())) {
            throw new TicketExceptions.NotAvailableException(requestDto.ticketId());
        }

        Booking booking = new Booking();
        booking.setUser(user);
        booking.setEvent(event);
        booking.setTicket(ticket);
        booking.setStatus(BookingStatus.ON_HOLD);
        booking.setRegisteredAt(LocalDateTime.now());

        return toDto(bookingRepository.save(booking));
    }

    @Override
    public BookingResponseDto confirmBooking(Long bookingId) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new BookingExceptions.NotFoundException(bookingId));

        if (booking.getStatus() != BookingStatus.ON_HOLD) {
            throw new BookingExceptions.StatusConflictException("confirm", booking.getId(),
                    booking.getStatus(), List.of(BookingStatus.ON_HOLD));
        }

        booking.setStatus(BookingStatus.CONFIRMED);
        ticketService.decreaseAvailability(booking.getTicket().getId());

        return toDto(bookingRepository.save(booking));
    }

    @Override
    public void cancelBooking(Long bookingId) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new BookingExceptions.NotFoundException(bookingId));

        if (booking.getStatus() != BookingStatus.ON_HOLD && booking.getStatus() != BookingStatus.CONFIRMED) {
            throw new BookingExceptions.StatusConflictException("cancel", bookingId, booking.getStatus(), List.of(BookingStatus.ON_HOLD, BookingStatus.CONFIRMED));
        }

        BookingStatus previousStatus = booking.getStatus();
        booking.setStatus(BookingStatus.CANCELED);

        if (previousStatus == BookingStatus.CONFIRMED) {
            ticketService.increaseAvailability(booking.getTicket().getId());
        }

        bookingRepository.save(booking);
    }

    @Override
    public BookingResponseDto getBookingById(Long bookingId) {
        return toDto(bookingRepository.findById(bookingId)
                .orElseThrow(() -> new BookingExceptions.NotFoundException(bookingId)));
    }

    @Override
    public List<BookingResponseDto> getAllBookings() {
        return bookingRepository.findAll()
                .stream()
                .map(this::toDto)
                .toList();
    }

    @Override
    public List<BookingResponseDto> getBookingsByUserId(Long userId) {
        userRepository.findById(userId)
                .orElseThrow(() -> new UserExceptions.NotFoundException(userId));
        return bookingRepository.findAllByUserId(userId)
                .stream()
                .map(this::toDto)
                .toList();
    }

    @Override
    public List<BookingResponseDto> getBookingsByEventId(Long eventId) {
        eventRepository.findById(eventId)
                .orElseThrow(() -> new EventExceptions.NotFoundExceptions(eventId));
        return bookingRepository.findAllByEventId(eventId)
                .stream()
                .map(this::toDto)
                .toList();
    }

    @Override
    public List<BookingResponseDto> getBookingsByStatus(BookingStatus status) {
        return bookingRepository.findAllByStatus(status)
                .stream()
                .map(this::toDto)
                .toList();
    }

    @Override
    public List<BookingResponseDto> getBookingsByUserIdAndStatus(Long userId, BookingStatus status) {
        userRepository.findById(userId)
                .orElseThrow(() -> new UserExceptions.NotFoundException(userId));
        return bookingRepository.findAllByUserIdAndStatus(userId, status)
                .stream()
                .map(this::toDto)
                .toList();
    }
}
