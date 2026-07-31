package com.example.Event.Management.Platform.unitTests;

import com.example.Event.Management.Platform.model.dto.LocationRequestDto;
import com.example.Event.Management.Platform.model.entity.Location;
import com.example.Event.Management.Platform.repository.LocationRepository;
import com.example.Event.Management.Platform.service.serviceImpl.LocationServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class LocationServiceTests {

    @Mock
    private LocationRepository repository;

    @InjectMocks
    private LocationServiceImpl service;

    private Location location;
    private LocationRequestDto request;

    @BeforeEach
    void setUp() {
        location = new Location(
                1L,
                null,
                "Calea Bucuresti",
                205,
                "Tg Jiu",
                "123432"
        );

        request = new LocationRequestDto(
                location.getStreetName(),
                location.getNumber(),
                location.getCity(),
                location.getZipCode()
        );
    }

    @Test
    void getOrCreateLocation_ShouldReturnExisting_WhenLocationFound() {
        when(repository.findByStreetNameAndNumberAndCityAndZipCode(
                request.streetName(),
                request.number(),
                request.city(),
                request.zipCode()
        )).thenReturn(Optional.of(location));

        Location result = service.getOrCreateLocation(request);

        assertEquals(location, result);

        verify(repository, never()).save(any());
    }

    @Test
    void getOrCreateLocation_ShouldCreateAndSave_WhenLocationNotFound() {
        when(repository.findByStreetNameAndNumberAndCityAndZipCode(
                request.streetName(),
                request.number(),
                request.city(),
                request.zipCode()
        )).thenReturn(Optional.empty());

        when(repository.save(any(Location.class))).thenReturn(location);

        Location result = service.getOrCreateLocation(request);

        assertEquals(location, result);

        Location expectedToSave = new Location(
                null,
                null,
                request.streetName(),
                request.number(),
                request.city(),
                request.zipCode()
        );

        verify(repository).save(eq(expectedToSave));
    }
}
