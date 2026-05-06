package com.example.Event.Management.Platform.model.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

public class TicketExceptions {
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public static class NotFoundException extends RuntimeException {
        public NotFoundException(Long id) {
            super("Ticket with id: " + id + " not found");
        }
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public static class InvalidDataException extends RuntimeException {
        public InvalidDataException(String field, String reason) {
            super("Invalid ticket data - " + field + ": " + reason);
        }
    }

    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public static class DataAccessException extends RuntimeException {
        public DataAccessException(Long eventId) {
            super("Failed to retrieve ticket data for event: " + eventId);
        }
    }

    @ResponseStatus(HttpStatus.CONFLICT)
    public static class CapacityExceededException extends RuntimeException {
        public CapacityExceededException(Integer maxParticipants) {
            super("Total tickets exceed event capacity of: " + maxParticipants);
        }
    }

    @ResponseStatus(HttpStatus.CONFLICT)
    public static class NotAvailableException extends RuntimeException {
        public NotAvailableException(Long ticketId) {
            super("Ticket with id: " + ticketId + " is not available");
        }
    }
}
