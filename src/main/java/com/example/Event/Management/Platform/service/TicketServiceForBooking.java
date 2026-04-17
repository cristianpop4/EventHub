package com.example.Event.Management.Platform.service;

public interface TicketServiceForBooking {
    boolean checkAvailability(Long ticketId, Integer quantity);
    void decreaseAvailability(Long ticketId, Integer quantity);
}
