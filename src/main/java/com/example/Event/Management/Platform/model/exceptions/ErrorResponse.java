package com.example.Event.Management.Platform.model.exceptions;

import java.time.Instant;
import java.util.Map;

public record ErrorResponse(
        String message,
        int status,
        Instant timestamp,
        String path,
        Map<String, String> errors
) {}
