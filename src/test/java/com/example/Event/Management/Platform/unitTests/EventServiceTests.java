package com.example.Event.Management.Platform.unitTests;

import com.example.Event.Management.Platform.model.dto.*;
import com.example.Event.Management.Platform.model.entity.Event;
import com.example.Event.Management.Platform.model.entity.Location;
import com.example.Event.Management.Platform.model.entity.User;
import com.example.Event.Management.Platform.model.enums.EventCategory;
import com.example.Event.Management.Platform.model.enums.Role;
import com.example.Event.Management.Platform.model.exceptions.EventExceptions;
import com.example.Event.Management.Platform.repository.EventRepository;
import com.example.Event.Management.Platform.repository.UserRepository;
import com.example.Event.Management.Platform.service.notification.MailService;
import com.example.Event.Management.Platform.service.serviceImpl.EventServiceImpl;
import com.example.Event.Management.Platform.service.serviceImpl.LocationServiceImpl;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class EventServiceTests {
    @Mock
    private EventRepository eventRepository;

    @Mock
    private LocationServiceImpl locationService;

    @Mock
    private UserRepository userRepository;

    @Mock
    private MailService mailService;

    @InjectMocks
    private EventServiceImpl eventService;

    private Event event;
    private EventRequestDto eventRequest;
    private Location location;
    private User user;

    @BeforeEach
    void setUp() {
        eventRequest = new EventRequestDto(
                "Music festival",
                "New music festival",
                EventCategory.MUSIC,
                new LocationRequestDto(
                        "Calea Bucuresti",
                        205,
                        "Tg Jiu",
                        "123432"
                ),
                LocalDateTime.of(2026, 6, 15, 12, 0, 0),
                200
        );

        user = new User(
                1L,
                "Organizer",
                "test@example.com",
                "Password12!",
                Role.ROLE_ADMIN
        );

        location = new Location(
                1L,
                null,
                "Calea Bucuresti",
                205,
                "Tg Jiu",
                "123432"
        );

        event = new Event(
                1L,
                eventRequest.name(),
                eventRequest.description(),
                eventRequest.eventCategory(),
                location,
                eventRequest.date(),
                eventRequest.maxParticipants(),
                user,
                null,
                null
        );

        Authentication authentication = mock(Authentication.class);
        SecurityContext securityContext = mock(SecurityContext.class);
        lenient().when(securityContext.getAuthentication()).thenReturn(authentication);
        lenient().when(authentication.getName()).thenReturn("test@example.com");
        SecurityContextHolder.setContext(securityContext);
    }

    @AfterEach
    void tearDown() {
        SecurityContextHolder.clearContext();
    }

    @Test
    void createEvent_shouldSaveEvent() {
        when(userRepository.findByEmail(user.getEmail()))
                .thenReturn(Optional.of(user));

        when(locationService.getOrCreateLocation(eventRequest.location()))
                .thenReturn(location);

        when(eventRepository.save(any(Event.class)))
                .thenReturn(event);

        EventResponseDto response = eventService.createEvent(eventRequest);

        assertNotNull(response);
        assertEquals(eventRequest.name(), response.name());
        assertEquals(eventRequest.description(), response.description());
        assertEquals(eventRequest.eventCategory(), response.eventCategory());
        assertEquals(eventRequest.date(), response.date());
        assertEquals(eventRequest.maxParticipants(), response.maxParticipants());

        verify(userRepository).findByEmail(user.getEmail());
        verify(locationService).getOrCreateLocation(eventRequest.location());
        verify(mailService, times(1)).sendEventCreatedMail(user, event);
        verify(eventRepository).save(any(Event.class));
    }

    @Test
    void createEvent_ShouldThrow_WhenUserNotFound() {
        when(userRepository.findByEmail(user.getEmail()))
                .thenReturn(Optional.empty());

        assertThrows(UsernameNotFoundException.class,
                () -> eventService.createEvent(eventRequest));

        verify(eventRepository, never()).save(any());
        verify(mailService, never()).sendEventCreatedMail(any(), any());
    }

    @Test
    void getAllCategories_ShouldReturnListOfCategories() {
        List<String> result = eventService.getAllCategories();

        assertNotNull(result);
        assertEquals(EventCategory.values().length, result.size());
        assertTrue(result.containsAll(
                Arrays.stream(EventCategory.values())
                        .map(Enum::name)
                        .toList()
        ));
    }

    @Test
    void getEventById_ShouldReturnEvent() {
        when(eventRepository.findById(1L))
                .thenReturn(Optional.of(event));

        EventResponseDto response = eventService.getEventById(1L);

        assertNotNull(response);
        assertEquals(1L, response.id());
    }

    @Test
    void getEventById_ShouldThrow_WhenEventNotFound() {
        when(eventRepository.findById(99L))
                .thenReturn(Optional.empty());

        assertThrows(EventExceptions.NotFoundExceptions.class,
                () -> eventService.getEventById(99L));
    }

    @Test
    void updateEvent_ShouldSaveEvent() {
        EventUpdateDto updateDto = new EventUpdateDto(
                "The best music festival",
                "The best music festival from the world",
                EventCategory.MUSIC,
                new LocationRequestDto(
                        "Calea Bucuresti",
                        206,
                        "Tg Jiu",
                        "123432"
                ),
                eventRequest.date(),
                300
        );

        when(eventRepository.findById(1L))
                .thenReturn(Optional.of(event));
        when(locationService.getOrCreateLocation(updateDto.location()))
                .thenReturn(location);
        when(eventRepository.save(any(Event.class)))
                .thenReturn(event);

        EventResponseDto updated = eventService.updateEvent(1L, updateDto);

        assertNotNull(updated);
        assertEquals(updated.name(), updateDto.name());
        assertEquals(updated.description(), updateDto.description());
        assertEquals(updated.eventCategory(), updateDto.eventCategory());
        assertEquals(updated.date(), updateDto.date());
        assertEquals(updated.maxParticipants(), updateDto.maxParticipants());

        verify(locationService).getOrCreateLocation(updateDto.location());
        verify(eventRepository).save(any(Event.class));
    }

    @Test
    void updateEvent_ShouldThrow_WhenEventNotFound() {
        when(eventRepository.findById(99L))
                .thenReturn(Optional.empty());

        assertThrows(EventExceptions.NotFoundExceptions.class,
                () -> eventService.getEventById(99L));

        verify(locationService, never()).getOrCreateLocation(any());
        verify(eventRepository, never()).save(any());
    }

    @Test
    void deleteEventById_ShouldDeleteEvent() {
        when(eventRepository.findById(1L))
                .thenReturn(Optional.of(event));

        eventService.deleteEventById(1L);

        verify(eventRepository).delete(event);
    }

    @Test
    void deleteEventById_ShouldThrow_WhenEventNotFound() {
        when(eventRepository.findById(99L))
                .thenReturn(Optional.empty());

        assertThrows(EventExceptions.NotFoundExceptions.class,
                () -> eventService.getEventById(99L));

        verify(eventRepository, never()).delete(any());
    }

    @Test
    void searchEvents_ShouldReturnListOfEvents() {
        EventSearchDto searchDto = new EventSearchDto(
                "festival",
                "Tg Jiu",
                EventCategory.MUSIC,
                LocalDateTime.of(2026, 6, 10, 12, 0, 0)
        );

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

        when(eventRepository.searchEvents(
                eq("festival"),
                eq("tg jiu"),
                eq("MUSIC"),
                eq(LocalDateTime.of(2026, 6, 10, 12, 0, 0).format(formatter)),
                any()
        )).thenReturn(List.of(event));

        List<EventResponseDto> result = eventService.searchEvents(searchDto);

        assertNotNull(result);
        assertFalse(result.isEmpty());
        assertEquals(1, result.size());

        verify(eventRepository).searchEvents(
                eq("festival"),
                eq("tg jiu"),
                eq("MUSIC"),
                eq(LocalDateTime.of(2026, 6, 10, 12, 0, 0).format(formatter)),
                any()
        );
    }

    @Test
    void searchEvents_WhenDtoIsNull() {
        EventSearchDto searchDto = new EventSearchDto(
                null,null,null,null
        );

        List<EventResponseDto> result = eventService.searchEvents(searchDto);

        verify(eventRepository).searchEvents(
                eq(null),
                eq(null),
                eq(null),
                eq(null),
                any()
        );
    }
}
