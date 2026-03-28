package com.example.Event.Management.Platform.dto;

public record UserResponseDto(
    Long id,
    String username,
    String email,
    String password
) {
}
