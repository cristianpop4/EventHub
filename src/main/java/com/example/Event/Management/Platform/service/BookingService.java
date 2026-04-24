package com.example.Event.Management.Platform.service;

import com.example.Event.Management.Platform.model.dto.*;
import com.example.Event.Management.Platform.model.entity.Booking;
import com.example.Event.Management.Platform.model.enums.BookingStatus;

import java.util.List;

public interface BookingService {
    BookingResponseDto createBooking(BookingRequestDto requestDto);
    BookingResponseDto getBookingById(Long bookingId);
    List<BookingResponseDto> getAllBookings();
    List<BookingResponseDto> getBookingsByUserId(Long userId);
    List<BookingResponseDto> getBookingsByEventId(Long eventId);
    List<BookingResponseDto> getBookingsByStatus(BookingStatus status);
    List<BookingResponseDto> getBookingsByUserIdAndStatus(Long userId, BookingStatus status);
    BookingResponseDto confirmBooking(Long bookingId);
    void cancelBooking(Long bookingId);

    default BookingResponseDto toDto(Booking booking){
        return new BookingResponseDto(
                booking.getId(),
                booking.getUser().getUsername(),
                new EventSummaryDto(
                        booking.getEvent().getId(),
                        booking.getEvent().getName(),
                        booking.getEvent().getDate(),
                        new LocationResponseDto(
                                booking.getEvent().getLocation().getId(),
                                booking.getEvent().getLocation().getStreetName(),
                                booking.getEvent().getLocation().getNumber(),
                                booking.getEvent().getLocation().getCity(),
                                booking.getEvent().getLocation().getZipCode()
                        )
                ),
                new TicketSummaryDto(
                        booking.getTicket().getId(),
                        booking.getTicket().getType(),
                        booking.getTicket().getPrice()
                ),
                booking.getStatus(),
                booking.getRegisteredAt()
        );
    }
}
