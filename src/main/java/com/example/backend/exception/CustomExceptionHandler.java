package com.example.backend.exception;

import java.util.ArrayList;
import java.util.List;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.ServletWebRequest;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@ControllerAdvice
public class CustomExceptionHandler extends ResponseEntityExceptionHandler {

    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException ex,
                                                                  HttpHeaders headers, HttpStatus status,
                                                                  WebRequest request) {
        List<String> errors = new ArrayList<>();
        ex.getBindingResult().getFieldErrors().forEach(fieldError ->
                errors.add(fieldError.getField() + ": " + fieldError.getDefaultMessage()));
        ex.getBindingResult().getGlobalErrors().forEach(objectError ->
                errors.add(objectError.getObjectName() + ": " + objectError.getDefaultMessage()));

        ApiError apiError = new ApiError(HttpStatus.BAD_REQUEST,
                ((ServletWebRequest) request).getRequest().getRequestURI(), "Invalid parameters", errors);
        return handleExceptionInternal(ex, apiError, headers, apiError.getStatus(), request);
    }

    @ExceptionHandler(UserAlreadyExistsException.class)
    protected ResponseEntity<Object> handleUserAlreadyExists(UserAlreadyExistsException ex,
                                                             ServletWebRequest request) {
        ApiError apiError = new ApiError(HttpStatus.CONFLICT, request.getRequest().getRequestURI(),
                "Could not register user", ex.getMessage());
        return ResponseEntity
                .status(apiError.getStatus())
                .body(apiError);
    }
}
