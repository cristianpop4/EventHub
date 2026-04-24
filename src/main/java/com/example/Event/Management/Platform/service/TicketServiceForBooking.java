package com.example.Event.Management.Platform.service;

public interface TicketServiceForBooking {
    boolean checkAvailability(Long ticketId);
    void decreaseAvailability(Long ticketId);
    void increaseAvailability(Long ticketId);
}
