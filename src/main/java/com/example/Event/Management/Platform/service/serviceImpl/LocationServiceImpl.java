package com.example.Event.Management.Platform.service.serviceImpl;

import com.example.Event.Management.Platform.model.dto.LocationRequestDto;
import com.example.Event.Management.Platform.model.entity.Location;
import com.example.Event.Management.Platform.repository.LocationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class LocationServiceImpl {
    private final LocationRepository repository;

    public Location getOrCreateLocation(LocationRequestDto request){
        return repository.findByStreetNameAndNumberAndCityAndZipCode(request.streetName(), request.number(), request.city(), request.zipCode())
                .orElseGet(()->{
                    Location location = new Location(null, null, request.streetName(), request.number(), request.city(), request.zipCode());
                    return repository.save(location);
                });

    }

}
