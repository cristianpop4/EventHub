package com.example.Event.Management.Platform.controller;

import com.example.Event.Management.Platform.model.dto.EventRequestDto;
import com.example.Event.Management.Platform.model.dto.EventResponseDto;
import com.example.Event.Management.Platform.model.dto.EventSearchDto;
import com.example.Event.Management.Platform.model.dto.EventUpdateDto;
import com.example.Event.Management.Platform.service.EventService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/events")
@RequiredArgsConstructor
public class EventController {
    private final EventService eventService;

    @Operation(summary = "Save an event in DB")
    @PostMapping
    public EventResponseDto createEvent(@RequestBody EventRequestDto eventRequest){
        return eventService.createEvent(eventRequest);
    }

    @Operation(summary = "Get all event categories")
    @GetMapping("/categories")
    public List<String> getAllCategories(){
        return eventService.getAllCategories();
    }

    @Operation(summary = "Get event by id")
    @GetMapping("/{id}")
    public EventResponseDto getEventById(@PathVariable Long id){
        return eventService.getEventById(id);
    }

    @Operation(summary = "Update event")
    @PutMapping("/{id}")
    public EventResponseDto updateEvent(@PathVariable Long id, @RequestBody EventUpdateDto dto){
        return eventService.updateEvent(id, dto);
    }

    @Operation(summary = "Partial update event")
    @PatchMapping("/{id}")
    public EventResponseDto partialUpdateEvent(@PathVariable Long id, @RequestBody EventUpdateDto dto){
        return eventService.partialUpdateEvent(id, dto);
    }

    @Operation(summary = "Delete event")
    @DeleteMapping("/{id}")
    public void deleteEventById(@PathVariable Long id){
        eventService.deleteEventById(id);
    }

    @Operation(summary = "Search event")
    @GetMapping("/search")
    public List<EventResponseDto> searchEvents(EventSearchDto search){
        return eventService.searchEvents(search);
    }

}
