package com.example.Event.Management.Platform.controller;

import com.example.Event.Management.Platform.model.dto.TicketRequestDto;
import com.example.Event.Management.Platform.model.dto.TicketResponseDto;
import com.example.Event.Management.Platform.model.dto.TicketUpdateDto;
import com.example.Event.Management.Platform.service.TicketServiceForController;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/tickets")
@RequiredArgsConstructor
public class TicketController {
    private final TicketServiceForController ticketServiceForController;

    @Operation(summary = "Create ticket")
    @PostMapping
    public TicketResponseDto createTicket(@RequestBody TicketRequestDto dto){
        return ticketServiceForController.createTicket(dto);
    }

    @Operation(summary = "Get tickets by eventId")
    @GetMapping("/eventIds/{id}")
    public List<TicketResponseDto> getTicketsByEventId(@PathVariable Long eventId){
        return ticketServiceForController.getTicketsByEventId(eventId);
    }

    @Operation(summary = "Get tickets by id")
    @GetMapping("/{id}")
    public TicketResponseDto getTicketById(@PathVariable Long ticketId){
        return ticketServiceForController.getTicketById(ticketId);
    }

    @Operation(summary = "Update ticket")
    @PutMapping("/update/{id}")
    public TicketResponseDto updateTicket(@PathVariable Long ticketId,@RequestBody TicketUpdateDto update){
        return ticketServiceForController.updateTicket(ticketId, update);
    }

    @Operation(summary = "Delete ticket")
    @DeleteMapping("/delete/{id}")
    public void deleteTicketById(@PathVariable Long ticketId){
        ticketServiceForController.deleteTicketById(ticketId);
    }

}
