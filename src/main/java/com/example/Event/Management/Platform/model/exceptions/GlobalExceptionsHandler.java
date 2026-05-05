package com.example.Event.Management.Platform.model.exceptions;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.Instant;

@RestControllerAdvice
public class GlobalExceptionsHandler {

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleAll(Exception exception, HttpServletRequest request){
        ResponseStatus annotation = exception.getClass().getAnnotation(ResponseStatus.class);

        HttpStatus status = (annotation != null) ? annotation.value() : HttpStatus.INTERNAL_SERVER_ERROR;

        ErrorResponse errorResponse = new ErrorResponse(
                exception.getMessage(),
                status.value(),
                Instant.now(),
                request.getRequestURI()
        );

        return new ResponseEntity<>(errorResponse, status);
    }
}
