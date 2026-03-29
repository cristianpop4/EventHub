package com.example.Event.Management.Platform.controller;

import com.example.Event.Management.Platform.dto.OrganizerRequestDto;
import com.example.Event.Management.Platform.dto.OrganizerResponseDto;
import com.example.Event.Management.Platform.dto.OrganizerUpdateDto;
import com.example.Event.Management.Platform.service.OrganizerService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/organizers")
@RequiredArgsConstructor
public class OrganizerController {
    private final OrganizerService service;

    @PostMapping
    public OrganizerResponseDto createOrganizer(@RequestBody OrganizerRequestDto dto){
        return service.createOrganizer(dto);
    }

    @GetMapping("{id}")
    public OrganizerResponseDto getOrganizerById(@PathVariable Long id){
        return service.getOrganizerById(id);
    }

    @GetMapping
    public List<OrganizerResponseDto> getAllOrganizers(){
        return service.getAllOrganizers();
    }

    @PutMapping("{id}")
    public OrganizerResponseDto update(@PathVariable Long id,@RequestBody OrganizerUpdateDto updateDto){
        return service.update(id, updateDto);
    }

    @DeleteMapping
    public void deleteOrganizerById(Long id){
        service.deleteOrganizerById(id);
    }
}
