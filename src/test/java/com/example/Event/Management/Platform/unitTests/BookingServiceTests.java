package com.example.Event.Management.Platform.unitTests;

import com.example.Event.Management.Platform.model.dto.BookingRequestDto;
import com.example.Event.Management.Platform.model.dto.BookingResponseDto;
import com.example.Event.Management.Platform.model.entity.*;
import com.example.Event.Management.Platform.model.enums.BookingStatus;
import com.example.Event.Management.Platform.model.enums.EventCategory;
import com.example.Event.Management.Platform.model.enums.Role;
import com.example.Event.Management.Platform.model.enums.TicketType;
import com.example.Event.Management.Platform.model.exceptions.BookingExceptions;
import com.example.Event.Management.Platform.model.exceptions.EventExceptions;
import com.example.Event.Management.Platform.model.exceptions.UserExceptions;
import com.example.Event.Management.Platform.repository.BookingRepository;
import com.example.Event.Management.Platform.repository.EventRepository;
import com.example.Event.Management.Platform.repository.TicketRepository;
import com.example.Event.Management.Platform.repository.UserRepository;
import com.example.Event.Management.Platform.service.TicketServiceForBooking;
import com.example.Event.Management.Platform.service.notification.MailService;
import com.example.Event.Management.Platform.service.serviceImpl.BookingServiceImpl;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class BookingServiceTests {
    @Mock
    private BookingRepository bookingRepository;

    @Mock
    private EventRepository eventRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private TicketRepository ticketRepository;

    @Mock
    private TicketServiceForBooking ticketService;

    @Mock
    private MailService mailService;

    @InjectMocks
    private BookingServiceImpl bookingService;

    private User user;
    private Event event;
    private Ticket ticket;
    private Booking booking;
    private BookingRequestDto requestDto;

    @BeforeEach
    void setUp() {
        user = new User(
                1L,
                "User",
                "test@example.com",
                "Password12!",
                Role.ROLE_USER
        );

        event = new Event(
                1L,
                "Music festival",
                "New music festival",
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
                user,
                null,
                null
        );

        ticket = new Ticket(
                1L,
                TicketType.STANDARD,
                89.99,
                200,
                event
        );

        requestDto = new BookingRequestDto(event.getId(), ticket.getId());

        booking = new Booking(
                1L,
                user,
                event,
                ticket,
                BookingStatus.ON_HOLD,
                LocalDateTime.now()
        );

        Authentication authentication = mock(Authentication.class);
        SecurityContext securityContext = mock(SecurityContext.class);
        lenient().when(securityContext.getAuthentication()).thenReturn(authentication);
        lenient().when(authentication.getName()).thenReturn("test@example.com");
        SecurityContextHolder.setContext(securityContext);
    }

    @AfterEach
    void tearDown() {
        SecurityContextHolder.clearContext();
    }

    @Test
    void createBooking_shouldSaveBooking() {
        when(userRepository.findByEmail(user.getEmail()))
                .thenReturn(Optional.of(user));
        when(eventRepository.findById(event.getId()))
                .thenReturn(Optional.of(event));
        when(ticketRepository.findById(ticket.getId()))
                .thenReturn(Optional.of(ticket));
        when(ticketService.checkAvailability(ticket.getId()))
                .thenReturn(true);
        when(bookingRepository.save(any(Booking.class)))
                .thenReturn(booking);
        BookingResponseDto response = bookingService.createBooking(requestDto);

        assertNotNull(response);
        assertEquals(requestDto.eventId(), event.getId());
        assertEquals(requestDto.ticketId(), ticket.getId());
        assertEquals(BookingStatus.ON_HOLD, response.status());
        assertEquals(user.getName(), response.username());

        verify(userRepository).findByEmail(user.getEmail());
        verify(eventRepository).findById(event.getId());
        verify(ticketRepository).findById(ticket.getId());
        verify(mailService, times(1)).sendBookingCreatedMail(booking);
        verify(bookingRepository).save(any(Booking.class));
    }

    @Test
    void createBooking_shouldThrow_WhenUserNotFound() {
        when(userRepository.findByEmail(user.getEmail()))
                .thenReturn(Optional.empty());

        assertThrows(UsernameNotFoundException.class,
                () -> bookingService.createBooking(requestDto));

        verify(bookingRepository, never()).save(any());
        verify(mailService, never()).sendBookingCreatedMail(any());
    }

    @Test
    void confirmBooking_shouldSetBookingStatusConfirm() {
        when(bookingRepository.findById(booking.getId()))
                .thenReturn(Optional.of(booking));
        when(bookingRepository.save(any(Booking.class)))
                .thenReturn(booking);
        doNothing().when(ticketService)
                .decreaseAvailability(ticket.getId());

        BookingResponseDto responseDto = bookingService.confirmBooking(booking.getId());

        assertNotNull(responseDto);
        assertEquals(BookingStatus.CONFIRMED, responseDto.status());

        verify(bookingRepository).findById(booking.getId());
        verify(ticketService).decreaseAvailability(ticket.getId());
        verify(mailService, times(1)).sendBookingConfirmedMail(any(Booking.class));
        verify(bookingRepository).save(any(Booking.class));
    }

    @Test
    void confirmBooking_shouldThrow_whenStatusIsNotOnHold() {
        booking.setStatus(BookingStatus.CONFIRMED);

        when(bookingRepository.findById(booking.getId()))
                .thenReturn(Optional.of(booking));

        assertThrows(BookingExceptions.StatusConflictException.class,
                () -> bookingService.confirmBooking(booking.getId()));

        verify(bookingRepository, never()).save(any());
        verify(ticketService, never()).decreaseAvailability(any());
        verify(mailService, never()).sendBookingConfirmedMail(any());
    }

    @Test
    void cancelBooking_shouldCancel_WhenStatusIsOnHold() {
        when(bookingRepository.findById(booking.getId()))
                .thenReturn(Optional.of(booking));
        when(bookingRepository.save(any(Booking.class)))
                .thenReturn(booking);

        bookingService.cancelBooking(booking.getId());

        assertEquals(BookingStatus.CANCELED, booking.getStatus());

        verify(bookingRepository).findById(booking.getId());
        verify(ticketService, never()).increaseAvailability(any());
        verify(mailService, times(1)).sendBookingCancelledByUserMail(any(Booking.class));
        verify(bookingRepository).save(any(Booking.class));
    }

    @Test
    void cancelBooking_shouldIncreaseAvailability_WhenStatusIsConfirmed() {
        booking.setStatus(BookingStatus.CONFIRMED);

        when(bookingRepository.findById(booking.getId()))
                .thenReturn(Optional.of(booking));
        when(bookingRepository.save(any(Booking.class)))
                .thenReturn(booking);
        doNothing().when(ticketService)
                .increaseAvailability(ticket.getId());

        bookingService.cancelBooking(booking.getId());

        assertEquals(BookingStatus.CANCELED, booking.getStatus());

        verify(bookingRepository).findById(booking.getId());
        verify(ticketService).increaseAvailability(ticket.getId());
        verify(mailService, times(1)).sendBookingCancelledByUserMail(any(Booking.class));
        verify(bookingRepository).save(any(Booking.class));
    }

    @Test
    void cancelBooking_shouldThrow_whenStatusIsAlreadyCanceled() {
        booking.setStatus(BookingStatus.CANCELED);

        when(bookingRepository.findById(booking.getId()))
                .thenReturn(Optional.of(booking));

        assertThrows(BookingExceptions.StatusConflictException.class,
                () -> bookingService.cancelBooking(booking.getId()));

        verify(bookingRepository, never()).save(any());
        verify(ticketService, never()).increaseAvailability(any());
        verify(mailService, never()).sendBookingCancelledByUserMail(any());
    }

    @Test
    void cancelExpiredBookings_shouldCancelAllExpiredBookings() {
        booking.setRegisteredAt(LocalDateTime.now().minusHours(25));
        booking.setStatus(BookingStatus.ON_HOLD);

        when(bookingRepository.findAllByStatusAndRegisteredAtBefore(
                eq(BookingStatus.ON_HOLD),
                any(LocalDateTime.class)
        )).thenReturn(List.of(booking));

        bookingService.cancelExpiredBookings();

        assertEquals(BookingStatus.CANCELED, booking.getStatus());

        verify(ticketService).increaseAvailability(ticket.getId());
        verify(bookingRepository).save(booking);
        verify(mailService).sendBookingAutoCancelledMail(booking);
    }

    @Test
    void cancelExpiredBookings_shouldDoNothing_WhenNoExpiredBookings() {
        booking.setStatus(BookingStatus.CONFIRMED);

        when(bookingRepository.findAllByStatusAndRegisteredAtBefore(
                eq(BookingStatus.ON_HOLD),
                any(LocalDateTime.class)
        )).thenReturn(List.of());

        bookingService.cancelExpiredBookings();

        verify(ticketService, never()).increaseAvailability(any());
        verify(bookingRepository, never()).save(any());
        verify(mailService, never()).sendBookingAutoCancelledMail(any());
    }

    @Test
    void getBookingById_shouldReturnBooking() {
        when(bookingRepository.findById(1L))
                .thenReturn(Optional.of(booking));

        BookingResponseDto response = bookingService.getBookingById(1L);

        assertNotNull(response);
        assertEquals(1L, response.bookingId());
    }

    @Test
    void getBookingById_shouldThrow_whenBookingNotFound() {
        when(bookingRepository.findById(99L))
                .thenReturn(Optional.empty());

        assertThrows(BookingExceptions.NotFoundException.class,
                () -> bookingService.getBookingById(99L));
    }

    @Test
    void getAllBookings_shouldReturnListOfBookings() {
        when(bookingRepository.findAll())
                .thenReturn(List.of(booking));

        List<BookingResponseDto> result = bookingService.getAllBookings();

        assertNotNull(result);
        assertEquals(1, result.size());

        verify(bookingRepository).findAll();
    }

    @Test
    void getAllBookings_shouldReturnEmptyList_whenNoBookings() {
        when(bookingRepository.findAll())
                .thenReturn(List.of());

        List<BookingResponseDto> result = bookingService.getAllBookings();

        assertNotNull(result);
        assertEquals(0, result.size());

        verify(bookingRepository).findAll();
    }

    @Test
    void getBookingsByUserId_shouldReturnListOfBookings() {
        when(userRepository.findById(user.getId()))
                .thenReturn(Optional.of(user));

        when(bookingRepository.findAllByUserId(user.getId()))
                .thenReturn(List.of(booking));

        List<BookingResponseDto> result = bookingService.getBookingsByUserId(user.getId());

        assertNotNull(result);
        assertEquals(1, result.size());

        verify(bookingRepository).findAllByUserId(user.getId());
    }

    @Test
    void getBookingsByUserId_shouldThrow_whenUserNotFound() {
        when(userRepository.findById(99L))
                .thenReturn(Optional.empty());

        assertThrows(UserExceptions.NotFoundException.class,
                () -> bookingService.getBookingsByUserId(99L));

        verify(bookingRepository, never()).findAllByUserId(99L);
    }

    @Test
    void getBookingsByEventId_shouldReturnListOfBookings() {
        when(eventRepository.findById(1L))
                .thenReturn(Optional.of(event));

        when(bookingRepository.findAllByEventId(event.getId()))
                .thenReturn(List.of(booking));

        List<BookingResponseDto> result = bookingService.getBookingsByEventId(event.getId());

        assertNotNull(result);
        assertEquals(1, result.size());

        verify(bookingRepository).findAllByEventId(event.getId());
    }

    @Test
    void getBookingsByEventId_shouldThrow_whenEventNotFound() {
        when(eventRepository.findById(99L))
                .thenReturn(Optional.empty());

        assertThrows(EventExceptions.NotFoundExceptions.class,
                () -> bookingService.getBookingsByEventId(99L));

        verify(bookingRepository, never()).findAllByEventId(99L);
    }

    @Test
    void getBookingsByStatus_shouldReturnListOfBookings() {
        when(bookingRepository.findAllByStatus(BookingStatus.ON_HOLD))
                .thenReturn(List.of(booking));

        List<BookingResponseDto> result = bookingService.getBookingsByStatus(BookingStatus.ON_HOLD);

        assertNotNull(result);
        assertEquals(1, result.size());

        verify(bookingRepository).findAllByStatus(BookingStatus.ON_HOLD);
    }

    @Test
    void getBookingsByStatus_shouldReturnEmptyList_WhenNoBookingWithThatStatus() {
        when(bookingRepository.findAllByStatus(BookingStatus.CONFIRMED))
                .thenReturn(List.of());

        List<BookingResponseDto> result = bookingService.getBookingsByStatus(BookingStatus.CONFIRMED);

        assertNotNull(result);
        assertEquals(0, result.size());

        verify(bookingRepository).findAllByStatus(BookingStatus.CONFIRMED);
    }

    @Test
    void getBookingsByUserIdAndStatus_ShouldReturnListOfBookings() {
        when(userRepository.findById(1L))
                .thenReturn(Optional.of(user));

        when(bookingRepository.findAllByUserIdAndStatus(1L, BookingStatus.ON_HOLD))
                .thenReturn(List.of(booking));

        List<BookingResponseDto> result = bookingService
                .getBookingsByUserIdAndStatus(1L, BookingStatus.ON_HOLD);

        assertNotNull(result);
        assertEquals(1, result.size());

        verify(bookingRepository).findAllByUserIdAndStatus(1L, BookingStatus.ON_HOLD);
    }

    @Test
    void getBookingsByUserIdAndStatus_ShouldReturnEmptyList_WhenUserHasNoBookingsWithThatStatus() {
        when(userRepository.findById(1L))
                .thenReturn(Optional.of(user));

        when(bookingRepository.findAllByUserIdAndStatus(1L, BookingStatus.CONFIRMED))
                .thenReturn(List.of());

        List<BookingResponseDto> result = bookingService
                .getBookingsByUserIdAndStatus(1L, BookingStatus.CONFIRMED);

        assertNotNull(result);
        assertEquals(0, result.size());

        verify(bookingRepository).findAllByUserIdAndStatus(1L, BookingStatus.CONFIRMED);
    }

    @Test
    void getBookingsByUserIdAndStatus_ShouldThrow_WhenUserNotFound() {
        when(userRepository.findById(99L))
                .thenReturn(Optional.empty());

        assertThrows(UserExceptions.NotFoundException.class,
                () -> bookingService.getBookingsByUserIdAndStatus(99L, BookingStatus.ON_HOLD));

        verifyNoInteractions(bookingRepository);
    }
}
