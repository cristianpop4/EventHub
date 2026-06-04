package com.example.Event.Management.Platform.model.dto;

public record AuthRequest(
        String email,
        String password
) { }
