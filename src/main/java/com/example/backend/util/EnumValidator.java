package com.example.backend.util;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.util.Arrays;
import java.util.stream.Collectors;

public class EnumValidator implements ConstraintValidator<Enum, String> {
    private Enum constraintAnnotation;

    @Override
    public void initialize(Enum constraintAnnotation) {
        this.constraintAnnotation = constraintAnnotation;
    }

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        if (value == null) return true;
        boolean valid = Arrays.stream(this.constraintAnnotation.enumClass().getEnumConstants())
                .anyMatch(enumValue -> enumValue.toString().equals(value) ||
                        this.constraintAnnotation.ignoreCase() &&
                                enumValue.toString().equalsIgnoreCase(value));
        if (!valid) {
            context.disableDefaultConstraintViolation();
            String errorMessage = "Can only be: " +
                    Arrays.stream(this.constraintAnnotation.enumClass().getEnumConstants())
                            .map(String::valueOf)
                            .collect(Collectors.joining(", "));
            context.buildConstraintViolationWithTemplate(errorMessage)
                    .addConstraintViolation();
        }
        return valid;
    }
}
