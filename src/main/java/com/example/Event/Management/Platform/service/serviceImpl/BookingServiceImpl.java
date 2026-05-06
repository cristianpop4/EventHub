package com.example.Event.Management.Platform.service.serviceImpl;

import com.example.Event.Management.Platform.model.dto.BookingRequestDto;
import com.example.Event.Management.Platform.model.dto.BookingResponseDto;
import com.example.Event.Management.Platform.model.entity.Booking;
import com.example.Event.Management.Platform.model.entity.Event;
import com.example.Event.Management.Platform.model.entity.Ticket;
import com.example.Event.Management.Platform.model.entity.User;
import com.example.Event.Management.Platform.model.enums.BookingStatus;
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
                .orElseThrow(() -> new UserExceptions.UserNotFoundException(requestDto.userId()));

        Event event = eventRepository.findById(requestDto.eventId())
                .orElseThrow(() -> new RuntimeException("Event not found"));

        Ticket ticket = ticketRepository.findById(requestDto.ticketId())
                .orElseThrow(() -> new RuntimeException("Ticket not found"));

        if (!ticketService.checkAvailability(requestDto.ticketId())) {
            throw new RuntimeException("There are no more tickets available");
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
                .orElseThrow(() -> new RuntimeException("Booking not found"));

        if (booking.getStatus() != BookingStatus.ON_HOLD) {
            throw new RuntimeException("Booking status already changed or cannot be confirm");
        }

        booking.setStatus(BookingStatus.CONFIRMED);
        ticketService.decreaseAvailability(booking.getTicket().getId());

        return toDto(bookingRepository.save(booking));
    }

    @Override
    public void cancelBooking(Long bookingId) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new RuntimeException("Booking not found"));

        if (booking.getStatus() != BookingStatus.ON_HOLD && booking.getStatus() != BookingStatus.CONFIRMED) {
            throw new RuntimeException("Booking status already changed or cannot be canceled");
        }

        booking.setStatus(BookingStatus.CANCELED);
        ticketService.increaseAvailability(booking.getTicket().getId());

        bookingRepository.save(booking);
    }

    @Override
    public BookingResponseDto getBookingById(Long bookingId) {
        return toDto(bookingRepository.findById(bookingId)
                .orElseThrow(() -> new RuntimeException("Booking not found")));
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
        return bookingRepository.findAllByUserId(userId)
                .stream()
                .map(this::toDto)
                .toList();
    }

    @Override
    public List<BookingResponseDto> getBookingsByEventId(Long eventId) {
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
        return bookingRepository.findAllByUserIdAndStatus(userId, status)
                .stream()
                .map(this::toDto)
                .toList();
    }
}
