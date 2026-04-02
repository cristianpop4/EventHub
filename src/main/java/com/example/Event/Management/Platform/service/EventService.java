package com.example.Event.Management.Platform.service;

import com.example.Event.Management.Platform.model.dto.EventRequestDto;
import com.example.Event.Management.Platform.model.dto.EventResponseDto;
import com.example.Event.Management.Platform.model.dto.LocationResponseDto;
import com.example.Event.Management.Platform.model.entity.Event;

public interface EventService {
    EventResponseDto createEvent(EventRequestDto eventRequest);

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
