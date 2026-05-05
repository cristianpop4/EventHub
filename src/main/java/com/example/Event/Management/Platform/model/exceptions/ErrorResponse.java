package com.example.Event.Management.Platform.model.exceptions;

import java.time.Instant;

public record ErrorResponse(
        String message,
        int status,
        Instant timestamp,
        String path
) { }
