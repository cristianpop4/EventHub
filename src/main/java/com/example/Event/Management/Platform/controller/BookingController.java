package com.example.Event.Management.Platform.controller;

import com.example.Event.Management.Platform.model.dto.BookingRequestDto;
import com.example.Event.Management.Platform.model.dto.BookingResponseDto;
import com.example.Event.Management.Platform.model.enums.BookingStatus;
import com.example.Event.Management.Platform.security.CustomUserDetails;
import com.example.Event.Management.Platform.service.BookingService;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/bookings")
@RequiredArgsConstructor
public class BookingController {
    private final BookingService bookingService;

    @Operation(summary = "Create booking")
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public BookingResponseDto createBooking(@Valid @RequestBody BookingRequestDto requestDto) {
        return bookingService.createBooking(requestDto);
    }

    @Operation(summary = "Get booking by id")
    @GetMapping("/{bookingId}")
    public BookingResponseDto getBookingById(
            @PathVariable Long bookingId,
            @AuthenticationPrincipal CustomUserDetails currentUser) {
        return bookingService.getBookingById(bookingId, currentUser);
    }

    @Operation(summary = "Get all bookings")
    @GetMapping
    public List<BookingResponseDto> getAllBookings() {
        return bookingService.getAllBookings();
    }

    @Operation(summary = "Get bookings by userId")
    @GetMapping("/users/{userId}")
    public List<BookingResponseDto> getBookingsByUserId(
            @PathVariable Long userId,
            @AuthenticationPrincipal CustomUserDetails currentUser) {
        return bookingService.getBookingsByUserId(userId, currentUser);
    }

    @Operation(summary = "Get bookings by eventId")
    @GetMapping("/events/{eventId}")
    public List<BookingResponseDto> getBookingsByEventId(@PathVariable Long eventId,
                                                         @AuthenticationPrincipal CustomUserDetails currentUser) {
        return bookingService.getBookingsByEventId(eventId, currentUser);
    }

    @Operation(summary = "Get bookings by status")
    @GetMapping("/status")
    public List<BookingResponseDto> getBookingsByStatus(@RequestParam BookingStatus status){
        return bookingService.getBookingsByStatus(status);
    }

    @Operation(summary = "Get bookings by userId and status")
    @GetMapping("/users/{userId}/status")
    public List<BookingResponseDto> getBookingsByUserIdAndStatus(@PathVariable Long userId,
                                                                 @RequestParam BookingStatus status,
                                                                 @AuthenticationPrincipal CustomUserDetails currentUser){
        return bookingService.getBookingsByUserIdAndStatus(userId, status, currentUser);
    }

        @Operation(summary = "Confirm booking")
        @PutMapping("/{bookingId}/confirm")
        public BookingResponseDto confirmBooking(@PathVariable Long bookingId,
                                                 @AuthenticationPrincipal CustomUserDetails currentUser){
            return bookingService.confirmBooking(bookingId, currentUser);
        }

        @Operation(summary = "Cancel booking")
        @PutMapping("/{bookingId}/cancel")
        @ResponseStatus(HttpStatus.NO_CONTENT)
        public void cancelBooking(@PathVariable Long bookingId,
                                  @AuthenticationPrincipal CustomUserDetails currentUser){
            bookingService.cancelBooking(bookingId, currentUser);
        }
}
