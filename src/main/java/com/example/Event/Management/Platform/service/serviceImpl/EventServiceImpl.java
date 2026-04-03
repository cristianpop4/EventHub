package com.example.Event.Management.Platform.service.serviceImpl;

import com.example.Event.Management.Platform.model.dto.EventRequestDto;
import com.example.Event.Management.Platform.model.dto.EventResponseDto;
import com.example.Event.Management.Platform.model.dto.EventSearchDto;
import com.example.Event.Management.Platform.model.dto.EventUpdateDto;
import com.example.Event.Management.Platform.model.entity.Event;
import com.example.Event.Management.Platform.model.entity.Location;
import com.example.Event.Management.Platform.model.entity.Organizer;
import com.example.Event.Management.Platform.model.enums.EventCategory;
import com.example.Event.Management.Platform.repository.EventRepository;
import com.example.Event.Management.Platform.repository.OrganizerRepository;
import com.example.Event.Management.Platform.service.EventService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

@Service
@RequiredArgsConstructor
public class EventServiceImpl implements EventService {
    private final EventRepository eventRepository;
    private final LocationServiceImpl locationService;
    private final OrganizerRepository organizerRepository;

    public EventResponseDto createEvent(EventRequestDto eventRequest){
        Organizer organizer = organizerRepository.findById(eventRequest.organizerId())
                .orElseThrow(()-> new RuntimeException("Organizer not found"));

        Location location = locationService.getOrCreateLocation(eventRequest.location());

        Event event = Event.builder()
                .name(eventRequest.name())
                .description(eventRequest.description())
                .eventCategory(eventRequest.eventCategory())
                .location(location)
                .date(eventRequest.date())
                .maxParticipants(eventRequest.maxParticipants())
                .organizer(organizer)
                .build();

        return toDto(eventRepository.save(event));
    }

    public List<String> getAllCategories(){
        return Arrays.stream(EventCategory.values())
                .map(Enum::name)
                .toList();
    }

    public EventResponseDto getEventById(Long id){
        return toDto(
                eventRepository.findById(id)
                        .orElseThrow(()-> new RuntimeException("Event with id: " + id + " not found"))
        );
    }

    public List<EventResponseDto> getAllEvents(){
        return eventRepository.findAll()
                .stream()
                .map(this::toDto)
                .toList();
    }

    public EventResponseDto updateEvent(Long id, EventUpdateDto dto){
        Event event = eventRepository.findById(id)
                .orElseThrow(()-> new RuntimeException("Organizer not found"));

        Location location = locationService.getOrCreateLocation(dto.location());

        Organizer organizer = organizerRepository.findById(dto.organizerId())
                        .orElseThrow(()-> new RuntimeException("Organizer not found"));

        event.setName(dto.name());
        event.setDescription(dto.description());
        event.setEventCategory(dto.eventCategory());
        event.setLocation(location);
        event.setDate(dto.date());
        event.setMaxParticipants(dto.maxParticipants());
        event.setOrganizer(organizer);

        return toDto(eventRepository.save(event));
    }

    public void deleteEventById(Long id){
        Event event = eventRepository.findById(id)
                .orElseThrow(()-> new RuntimeException("Event not found"));

        eventRepository.delete(event);
    }

    public List<EventResponseDto> searchEvents(EventSearchDto search){
        return eventRepository.searchEvents(
                search.city().toUpperCase(),
                search.eventCategory(),
                search.dateTime(),
                LocalDateTime.now()
                )
                .stream()
                .map(this::toDto)
                .toList();
    }
}
