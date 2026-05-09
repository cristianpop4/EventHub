package com.example.Event.Management.Platform.model.dto;

public record UserResponseDto(
    Long id,
    String username,
    String email,
    String role
) { }
