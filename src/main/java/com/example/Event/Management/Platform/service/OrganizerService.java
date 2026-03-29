package com.example.Event.Management.Platform.service;

import com.example.Event.Management.Platform.dto.OrganizerRequestDto;
import com.example.Event.Management.Platform.dto.OrganizerResponseDto;
import com.example.Event.Management.Platform.dto.OrganizerUpdateDto;
import com.example.Event.Management.Platform.entity.Organizer;

import java.util.List;

public interface OrganizerService {
    OrganizerResponseDto createOrganizer(OrganizerRequestDto request);
    OrganizerResponseDto getOrganizerById(Long id);
    List<OrganizerResponseDto> getAllOrganizers();
    OrganizerResponseDto update(Long id, OrganizerUpdateDto updateDto);
    void deleteOrganizerById(Long id);

    default OrganizerResponseDto toDto (Organizer organizer){
        return new OrganizerResponseDto(
                organizer.getId(),
                organizer.getUsername(),
                organizer.getEmail(),
                organizer.getPassword()
        );
    }
}
