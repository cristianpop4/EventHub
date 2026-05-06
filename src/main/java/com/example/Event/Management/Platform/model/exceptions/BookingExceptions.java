package com.example.Event.Management.Platform.model.exceptions;

import com.example.Event.Management.Platform.model.enums.BookingStatus;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.util.List;
import java.util.stream.Collectors;

public class BookingExceptions {

    @ResponseStatus(HttpStatus.NOT_FOUND)
    public static class NotFoundException extends RuntimeException {
        public NotFoundException(Long bookingId) {
            super("Booking with id: " + bookingId + " not found");
        }
    }

    @ResponseStatus(HttpStatus.CONFLICT)
    public static class StatusConflictException extends RuntimeException {
        public StatusConflictException(String request, Long bookingId,
                                       BookingStatus currentStatus, List<BookingStatus> expectedStatuses) {
            super("Cannot " + request + " booking: " + bookingId
                    + " - current status is " + currentStatus
                    + ", expected: " + expectedStatuses.stream()
                    .map(Enum::name)
                    .collect(Collectors.joining(", ")));
        }
    }
}
