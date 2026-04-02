package com.example.Event.Management.Platform.service;

import com.example.Event.Management.Platform.model.dto.LocationRequestDto;
import com.example.Event.Management.Platform.model.entity.Location;

public interface LocationService {
    Location getOrCreateLocation(LocationRequestDto request);
}
