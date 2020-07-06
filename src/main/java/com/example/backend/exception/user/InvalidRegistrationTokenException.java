package com.example.backend.exception.user;

public class InvalidRegistrationTokenException extends RuntimeException {
    public InvalidRegistrationTokenException(String message) {
        super(message);
    }
}
