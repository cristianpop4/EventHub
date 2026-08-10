package com.example.Event.Management.Platform.service.serviceImpl;

import com.example.Event.Management.Platform.model.dto.EventRequestDto;
import com.example.Event.Management.Platform.model.dto.EventResponseDto;
import com.example.Event.Management.Platform.model.dto.EventSearchDto;
import com.example.Event.Management.Platform.model.dto.EventUpdateDto;
import com.example.Event.Management.Platform.model.entity.Event;
import com.example.Event.Management.Platform.model.entity.Location;
import com.example.Event.Management.Platform.model.entity.User;
import com.example.Event.Management.Platform.model.enums.EventCategory;
import com.example.Event.Management.Platform.model.enums.Role;
import com.example.Event.Management.Platform.model.exceptions.EventExceptions;
import com.example.Event.Management.Platform.model.exceptions.ForbiddenException;
import com.example.Event.Management.Platform.repository.EventRepository;
import com.example.Event.Management.Platform.repository.UserRepository;
import com.example.Event.Management.Platform.security.CustomUserDetails;
import com.example.Event.Management.Platform.service.EventService;
import com.example.Event.Management.Platform.service.notification.MailService;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.NotNull;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class EventServiceImpl implements EventService {
    private final EventRepository eventRepository;
    private final LocationServiceImpl locationService;
    private final UserRepository userRepository;
    private final MailService mailService;

    @Override
    public EventResponseDto createEvent(@NotNull EventRequestDto eventRequest) {
        String email = Objects.requireNonNull(SecurityContextHolder.getContext()
                        .getAuthentication())
                .getName();

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        Location location = locationService.getOrCreateLocation(eventRequest.location());

        Event event = Event.builder()
                .name(eventRequest.name())
                .description(eventRequest.description())
                .eventCategory(eventRequest.eventCategory())
                .location(location)
                .date(eventRequest.date())
                .maxParticipants(eventRequest.maxParticipants())
                .user(user)
                .build();

        Event savedEvent = eventRepository.save(event);

        EventResponseDto dto = toDto(savedEvent);

        mailService.sendEventCreatedMail(user, savedEvent);

        return dto;
    }

    @Override
    public List<String> getAllCategories() {
        return Arrays.stream(EventCategory.values())
                .map(Enum::name)
                .toList();
    }

    @Override
    public EventResponseDto getEventById(Long id) {
        return toDto(
                eventRepository.findById(id)
                        .orElseThrow(() -> new EventExceptions.NotFoundExceptions(id))
        );
    }

    @Override
    public EventResponseDto updateEvent(Long id, @NotNull EventUpdateDto dto, CustomUserDetails currentUser) {
        Event event = eventRepository.findById(id)
                .orElseThrow(() -> new EventExceptions.NotFoundExceptions(id));

        boolean isOrganizer = event.getUser().getId().equals(currentUser.getId());
        boolean isAdmin = currentUser.getRole().equals(Role.ROLE_ADMIN);

        if (!isAdmin && !isOrganizer){
            throw new ForbiddenException();
        }

        Location location = locationService.getOrCreateLocation(dto.location());

        event.setName(dto.name());
        event.setDescription(dto.description());
        event.setEventCategory(dto.eventCategory());
        event.setLocation(location);
        event.setDate(dto.date());
        event.setMaxParticipants(dto.maxParticipants());

        return toDto(eventRepository.save(event));
    }

    @Override
    public EventResponseDto partialUpdateEvent(Long id, @NotNull EventUpdateDto dto, CustomUserDetails currentUser) {
        Event event = eventRepository.findById(id)
                .orElseThrow(() -> new EventExceptions.NotFoundExceptions(id));

        boolean isOrganizer = event.getUser().getId().equals(currentUser.getId());
        boolean isAdmin = currentUser.getRole().equals(Role.ROLE_ADMIN);

        if (!isAdmin && !isOrganizer){
            throw new ForbiddenException();
        }

        if (dto.name() != null) event.setName(dto.name());
        if (dto.description() != null) event.setDescription(dto.description());
        if (dto.eventCategory() != null) event.setEventCategory(dto.eventCategory());
        if (dto.location() != null) {
            Location location = locationService.getOrCreateLocation(dto.location());
            event.setLocation(location);
        }
        if (dto.date() != null) event.setDate(dto.date());
        if (dto.maxParticipants() != null) event.setMaxParticipants(dto.maxParticipants());

        return toDto(eventRepository.save(event));
    }

    @Override
    public void deleteEventById(Long id, CustomUserDetails currentUser) {
        Event event = eventRepository.findById(id)
                .orElseThrow(() -> new EventExceptions.NotFoundExceptions(id));

        boolean isOrganizer = event.getUser().getId().equals(currentUser.getId());
        boolean isAdmin = currentUser.getRole().equals(Role.ROLE_ADMIN);

        if (!isAdmin && !isOrganizer){
            throw new ForbiddenException();
        }

        eventRepository.delete(event);
    }

    @Override
    public List<EventResponseDto> searchEvents(@NotNull EventSearchDto search) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

        return eventRepository.searchEvents(
                        search.name() != null ? search.name().toLowerCase() : null,
                        search.city() != null ? search.city().toLowerCase() : null,
                        search.eventCategory() != null ? search.eventCategory().name() : null,
                        search.dateTime() != null ? search.dateTime().format(formatter) : null,
                        LocalDateTime.now().format(formatter)
                )
                .stream()
                .map(this::toDto)
                .toList();
    }
}
