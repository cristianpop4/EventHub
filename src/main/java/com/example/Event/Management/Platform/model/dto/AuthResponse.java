package com.example.Event.Management.Platform.model.dto;

public record AuthResponse(
        String token,
        String email,
        String role
) { }
