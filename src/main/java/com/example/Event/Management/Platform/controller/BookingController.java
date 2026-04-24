package com.example.Event.Management.Platform.controller;

import com.example.Event.Management.Platform.model.dto.BookingRequestDto;
import com.example.Event.Management.Platform.model.dto.BookingResponseDto;
import com.example.Event.Management.Platform.model.enums.BookingStatus;
import com.example.Event.Management.Platform.service.BookingService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/bookings")
@RequiredArgsConstructor
public class BookingController {
    private final BookingService bookingService;

    @Operation(summary = "Create booking")
    @PostMapping
    public BookingResponseDto createBooking(@RequestBody BookingRequestDto requestDto) {
        return bookingService.createBooking(requestDto);
    }

    @Operation(summary = "Get booking by id")
    @GetMapping("/{id}")
    public BookingResponseDto getBookingById(@PathVariable Long bookingId) {
        return bookingService.getBookingById(bookingId);
    }

    @Operation(summary = "Get all bookings")
    @GetMapping
    public List<BookingResponseDto> getAllBookings() {
        return bookingService.getAllBookings();
    }

    @Operation(summary = "Get bookings by userId")
    @GetMapping("/users/{id}")
    public List<BookingResponseDto> getBookingsByUserId(@PathVariable Long userId) {
        return bookingService.getBookingsByUserId(userId);
    }

    @Operation(summary = "Get bookings by eventId")
    @GetMapping("/events/{id}")
    public List<BookingResponseDto> getBookingsByEventId(@PathVariable Long eventId) {
        return bookingService.getBookingsByEventId(eventId);
    }

    @Operation(summary = "Get bookings by status")
    @GetMapping("/statuses")
    public List<BookingResponseDto> getBookingsByStatus(@RequestParam BookingStatus status){
        return bookingService.getBookingsByStatus(status);
    }

    @Operation(summary = "Get bookings by userId and status")
    @GetMapping("/statuses/users/{id}")
    public List<BookingResponseDto> getBookingsByUserIdAndStatus(@PathVariable Long userId,@RequestParam BookingStatus status){
        return bookingService.getBookingsByUserIdAndStatus(userId, status);
    }

    @Operation(summary = "Confirm booking")
    @PutMapping("/confirmations/{id}")
    public BookingResponseDto confirmBooking(@PathVariable Long bookingId){
        return bookingService.confirmBooking(bookingId);
    }

    @Operation(summary = "Cancel booking")
    @PutMapping("/cancellations/{id}")
    public void cancelBooking(@PathVariable Long bookingId){
        bookingService.cancelBooking(bookingId);
    }
}
