package com.example.backend.exception;

import java.util.Collections;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonInclude;
import org.springframework.http.HttpStatus;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class ApiError {
    private HttpStatus status;
    private String path;
    private String message;
    private List<String> errors;

    public ApiError(HttpStatus status, String path, String message, List<String> errors) {
        this.status = status;
        this.path = path;
        this.message = message;
        this.errors = errors;
    }

    public ApiError(HttpStatus status, String path, String message, String error) {
        this.status = status;
        this.path = path;
        this.message = message;
        errors = Collections.singletonList(error);
    }

    public ApiError(HttpStatus status, String path, String message) {
        this.status = status;
        this.path = path;
        this.message = message;
    }

    public HttpStatus getStatus() {
        return status;
    }

    public String getMessage() {
        return message;
    }

    public List<String> getErrors() {
        return errors;
    }

    public String getPath() {
        return path;
    }
}
