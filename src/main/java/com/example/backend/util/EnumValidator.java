package com.example.backend.util;

import java.util.Arrays;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class EnumValidator implements ConstraintValidator<Enum, String> {
    private Enum constraintAnnotation;

    @Override
    public void initialize(Enum constraintAnnotation) {
        this.constraintAnnotation = constraintAnnotation;
    }

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        if (value == null)
            return true;

        Predicate<java.lang.Enum<?>> predicate;
        if (constraintAnnotation.ignoreCase()) {
            predicate = enumValue -> enumValue.toString().equalsIgnoreCase(value);
        } else {
            predicate = enumValue -> enumValue.toString().equals(value);
        }
        boolean valid = Arrays.stream(this.constraintAnnotation.enumClass().getEnumConstants())
                              .anyMatch(predicate);
        if (!valid) {
            setCustomConstraintViolation(context);
        }
        return valid;
    }

    private void setCustomConstraintViolation(ConstraintValidatorContext context) {
        context.disableDefaultConstraintViolation();
        String errorMessage = "Can only be one of: " +
                Arrays.stream(this.constraintAnnotation.enumClass().getEnumConstants())
                      .map(String::valueOf)
                      .collect(Collectors.joining(", "));
        errorMessage += constraintAnnotation.ignoreCase() ? ". Case insensitive" : ". Case sensitive";
        context.buildConstraintViolationWithTemplate(errorMessage)
               .addConstraintViolation();
    }
}
