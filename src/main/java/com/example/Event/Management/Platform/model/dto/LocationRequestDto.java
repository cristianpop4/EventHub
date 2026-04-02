package com.example.Event.Management.Platform.model.dto;

public record LocationRequestDto(
        String streetName,
        Integer number,
        String city,
        String zipCode
) { }
