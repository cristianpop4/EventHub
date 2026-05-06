package com.example.Event.Management.Platform.service.serviceImpl;

import com.example.Event.Management.Platform.model.dto.OrganizerRequestDto;
import com.example.Event.Management.Platform.model.dto.OrganizerResponseDto;
import com.example.Event.Management.Platform.model.dto.OrganizerUpdateDto;
import com.example.Event.Management.Platform.model.entity.Organizer;
import com.example.Event.Management.Platform.model.exceptions.UserExceptions;
import com.example.Event.Management.Platform.repository.OrganizerRepository;
import com.example.Event.Management.Platform.service.OrganizerService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class OrganizerServiceImpl implements OrganizerService {
    private final OrganizerRepository repository;

    @Override
    public OrganizerResponseDto createOrganizer(OrganizerRequestDto request) {
        repository.findByEmail(request.email())
                .ifPresent(organizer -> {
                    throw new UserExceptions.EmailAlreadyExistsException(request.email());
                });

        Organizer organizer = Organizer.builder()
                .username(request.username())
                .email(request.email())
                .password(request.password())
                .build();

        return toDto(repository.save(organizer));
    }

    @Override
    public OrganizerResponseDto getOrganizerById(Long id) {
        return toDto(
                repository.findById(id)
                        .orElseThrow(() -> new UserExceptions.UserNotFoundException(id))
        );
    }

    @Override
    public List<OrganizerResponseDto> getAllOrganizers() {
        return repository.findAll()
                .stream()
                .map(this::toDto)
                .toList();
    }

    @Override
    public OrganizerResponseDto update(Long id,OrganizerUpdateDto updateDto){
        Organizer organizer = repository.findById(id)
                .orElseThrow(()-> new UserExceptions.UserNotFoundException(id));

        organizer.setUsername(updateDto.username());
        organizer.setEmail(updateDto.email());
        organizer.setPassword(updateDto.password());

        return toDto(repository.save(organizer));
    }

    @Override
    public void deleteOrganizerById(Long id){
        Organizer organizer = repository.findById(id)
                .orElseThrow(()-> new UserExceptions.UserNotFoundException(id));

        repository.delete(organizer);
    }
}
