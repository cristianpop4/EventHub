package com.example.Event.Management.Platform.service.serviceImpl;

import com.example.Event.Management.Platform.dto.OrganizerRequestDto;
import com.example.Event.Management.Platform.dto.OrganizerResponseDto;
import com.example.Event.Management.Platform.dto.OrganizerUpdateDto;
import com.example.Event.Management.Platform.entity.Organizer;
import com.example.Event.Management.Platform.repository.OrganizerRepository;
import com.example.Event.Management.Platform.service.OrganizerService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class OrganizerServiceImpl implements OrganizerService {
    private final OrganizerRepository repository;

    public OrganizerResponseDto createOrganizer(OrganizerRequestDto request) {
        repository.findByEmail(request.email())
                .ifPresent(organizer -> {
                    throw new RuntimeException("User with this email already exists");
                });

        Organizer organizer = Organizer.builder()
                .username(request.username())
                .email(request.email())
                .password(request.password())
                .build();

        return toDto(repository.save(organizer));
    }

    public OrganizerResponseDto getOrganizerById(Long id) {
        return toDto(
                repository.findById(id)
                        .orElseThrow(() -> new RuntimeException("Organizer not found"))
        );
    }

    public List<OrganizerResponseDto> getAllOrganizers() {
        return repository.findAll()
                .stream()
                .map(this::toDto)
                .toList();
    }

    public OrganizerResponseDto update(Long id,OrganizerUpdateDto updateDto){
        Organizer organizer = repository.findById(id)
                .orElseThrow(()-> new RuntimeException("Organizer not found"));

        organizer.setUsername(updateDto.username());
        organizer.setEmail(updateDto.email());
        organizer.setPassword(updateDto.password());

        return toDto(repository.save(organizer));
    }

    public void deleteOrganizerById(Long id){
        Organizer organizer = repository.findById(id)
                .orElseThrow(()-> new RuntimeException("Organizer not found"));

        repository.delete(organizer);
    }
}
