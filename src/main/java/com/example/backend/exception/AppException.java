package com.example.backend.exception;

public class AppException extends RuntimeException {
    private final ExceptionType type;

    public AppException(String message, ExceptionType type) {
        super(message);
        this.type = type;
    }

    public ExceptionType getType() {
        return type;
    }
}
