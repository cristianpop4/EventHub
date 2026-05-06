package com.example.Event.Management.Platform.model.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

public class EventExceptions {
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public static class NotFoundExceptions extends RuntimeException{
        public NotFoundExceptions(Long id){
            super("Event with id: " + id + " not found");
        }
    }
}
