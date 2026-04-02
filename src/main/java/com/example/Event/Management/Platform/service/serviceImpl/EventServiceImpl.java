package com.example.Event.Management.Platform.service.serviceImpl;

import com.example.Event.Management.Platform.model.dto.EventRequestDto;
import com.example.Event.Management.Platform.model.dto.EventResponseDto;
import com.example.Event.Management.Platform.model.entity.Event;
import com.example.Event.Management.Platform.model.entity.Location;
import com.example.Event.Management.Platform.model.entity.Organizer;
import com.example.Event.Management.Platform.model.enums.EventCategory;
import com.example.Event.Management.Platform.repository.EventRepository;
import com.example.Event.Management.Platform.repository.OrganizerRepository;
import com.example.Event.Management.Platform.service.EventService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

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


}
