package com.example.Event.Management.Platform.model.dto;

public record LocationResponseDto(
        Long id,
        String streetName,
        Integer number,
        String city,
        String zipCode
) { }
