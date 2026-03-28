package com.example.Event.Management.Platform.dto;

public record UserRequestDto(
    String username,
    String email,
    String password
) {
}
