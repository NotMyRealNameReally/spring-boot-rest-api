package com.example.backend.exception;

import org.assertj.core.api.Condition;

import static com.example.backend.exception.ExceptionType.INVALID_PASSWORD;
import static com.example.backend.exception.ExceptionType.INVALID_REGISTRATION_TOKEN;
import static com.example.backend.exception.ExceptionType.USER_ALREADY_EXISTS;

public abstract class ExceptionConditions {
    public static final Condition<AppException> INVALID_PASSWORD_EXCEPTION =
            new Condition<>(e -> INVALID_PASSWORD.equals(e.getType()), "invalid password");

    public static final Condition<AppException> INVALID_REGISTRATION_TOKEN_EXCEPTION =
            new Condition<>(e -> INVALID_REGISTRATION_TOKEN.equals(e.getType()), "invalid registration token");

    public static final Condition<AppException> USER_ALREADY_EXISTS_EXCEPTION =
            new Condition<>(e -> USER_ALREADY_EXISTS.equals(e.getType()), "user already exists");
}
