package com.example.Event.Management.Platform.dto;

public record UserUpdateDto(
        String username,
        String email,
        String password
) { }
