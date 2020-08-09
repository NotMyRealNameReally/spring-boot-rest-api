package com.example.backend.util;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

public class DateValidator implements ConstraintValidator<Date, String> {
    private Date constraintAnnotation;

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        if (value == null) return true;
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(constraintAnnotation.pattern());
        LocalDate validated;

        try {
            validated = LocalDate.parse(value, formatter);
        } catch (DateTimeParseException e) {
            setCustomConstraintViolation("Should be of pattern: " + constraintAnnotation.pattern(), context);
            return false;
        }
        if (constraintAnnotation.notPast() && validated.isBefore(LocalDate.now())) {
            setCustomConstraintViolation("Cannot be in the past", context);
            return false;
        }
        return true;
    }

    private void setCustomConstraintViolation(String message, ConstraintValidatorContext context) {
        context.disableDefaultConstraintViolation();
        context.buildConstraintViolationWithTemplate(message)
                .addConstraintViolation();
    }

    @Override
    public void initialize(Date constraintAnnotation) {
        this.constraintAnnotation = constraintAnnotation;
    }
}
