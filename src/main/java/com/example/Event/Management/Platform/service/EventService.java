package com.example.Event.Management.Platform.service;

import com.example.Event.Management.Platform.model.dto.*;
import com.example.Event.Management.Platform.model.entity.Event;

import java.util.List;

public interface EventService {
    EventResponseDto createEvent(EventRequestDto eventRequest);
    List<String> getAllCategories();
    EventResponseDto getEventById(Long id);
    List<EventResponseDto> getAllEvents();
    EventResponseDto updateEvent(Long id, EventUpdateDto dto);
    void deleteEventById(Long id);
    List<EventResponseDto> searchEvents(EventSearchDto search);

    default EventResponseDto toDto(Event event){
        return new EventResponseDto(
                event.getId(),
                event.getName(),
                event.getDescription(),
                event.getEventCategory(),
                new LocationResponseDto(
                        event.getLocation().getId(),
                        event.getLocation().getStreetName(),
                        event.getLocation().getNumber(),
                        event.getLocation().getCity(),
                        event.getLocation().getZipCode()
                ),
                event.getDate(),
                event.getMaxParticipants(),
                event.getOrganizer().getUsername()
        );
    }
}
