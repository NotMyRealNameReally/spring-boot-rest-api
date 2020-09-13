package com.example.backend.exception;

import java.util.ArrayList;
import java.util.List;

import javax.validation.ConstraintViolationException;

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
        ex.getBindingResult()
          .getFieldErrors()
          .forEach(fieldError -> errors.add(fieldError.getField() + ": " + fieldError.getDefaultMessage()));
        ex.getBindingResult()
          .getGlobalErrors()
          .forEach(objectError -> errors.add(objectError.getObjectName() + ": " + objectError.getDefaultMessage()));

        ApiError apiError = new ApiError(HttpStatus.BAD_REQUEST,
                ((ServletWebRequest) request).getRequest().getRequestURI(), "Invalid parameters", errors);
        return handleExceptionInternal(ex, apiError, headers, apiError.getStatus(), request);
    }

    @ExceptionHandler(ConstraintViolationException.class)
    protected ResponseEntity<Object> handleConstraintViolation(ConstraintViolationException ex,
                                                               ServletWebRequest request) {
        List<String> errors = new ArrayList<>();
        ex.getConstraintViolations()
          .forEach(violation -> errors.add(violation.getPropertyPath() + ": " + violation.getMessage()));

        ApiError apiError = new ApiError(HttpStatus.BAD_REQUEST, request.getRequest().getRequestURI(),
                "Invalid parameters", errors);
        return ResponseEntity
                .status(apiError.getStatus())
                .body(apiError);
    }

    @ExceptionHandler(AppException.class)
    protected ResponseEntity<Object> handleAppException(AppException ex, ServletWebRequest request) {
        return switch (ex.getType()) {
            case USER_ALREADY_EXISTS -> handleUserAlreadyExists(ex, request);
            case INVALID_REGISTRATION_TOKEN -> handleInvalidRegistrationToken(ex, request);
            case INVALID_PASSWORD -> handleInvalidPassword(ex, request);
        };
    }

    private ResponseEntity<Object> handleUserAlreadyExists(AppException ex,
                                                           ServletWebRequest request) {
        ApiError apiError = new ApiError(HttpStatus.CONFLICT, request.getRequest().getRequestURI(),
                "Could not register user", ex.getMessage());
        return ResponseEntity
                .status(apiError.getStatus())
                .body(apiError);
    }

    private ResponseEntity<Object> handleInvalidRegistrationToken(AppException ex,
                                                                  ServletWebRequest request) {
        ApiError apiError = new ApiError(HttpStatus.BAD_REQUEST, request.getRequest().getRequestURI(),
                "Could not register user.", ex.getMessage());
        return ResponseEntity
                .status(apiError.getStatus())
                .body(apiError);
    }

    private ResponseEntity<Object> handleInvalidPassword(AppException ex, ServletWebRequest request) {
        ApiError apiError = new ApiError(HttpStatus.BAD_REQUEST, request.getRequest().getRequestURI(), ex.getMessage());
        return ResponseEntity
                .status(apiError.getStatus())
                .body(apiError);
    }
}
