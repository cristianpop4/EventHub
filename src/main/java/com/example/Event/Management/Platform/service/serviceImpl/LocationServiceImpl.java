package com.example.Event.Management.Platform.service.serviceImpl;

import com.example.Event.Management.Platform.model.dto.LocationRequestDto;
import com.example.Event.Management.Platform.model.entity.Location;
import com.example.Event.Management.Platform.repository.LocationRepository;
import com.example.Event.Management.Platform.service.LocationService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class LocationServiceImpl implements LocationService {
    private final LocationRepository repository;

    public Location getOrCreateLocation(LocationRequestDto request){
        return repository.findByStreetNameNumberCityZipCode(request.streetName(), request.number(), request.city(), request.zipCode())
                .orElseGet(()->{
                    Location location = new Location(null, null, request.streetName(), request.number(), request.city(), request.zipCode());
                    return repository.save(location);
                });

    }

}
