package com.example.Event.Management.Platform.model.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

public class UserExceptions {

    @ResponseStatus(HttpStatus.CONFLICT)
    public static class EmailAlreadyExistsException extends RuntimeException{
        public EmailAlreadyExistsException(String email){
            super("Email already exists: " + email);
        }
    }

    @ResponseStatus(HttpStatus.NOT_FOUND)
    public static class UserNotFoundException extends RuntimeException{
        public UserNotFoundException(Long id){
            super("User with id: " + id + " not found");
        }
    }
}
