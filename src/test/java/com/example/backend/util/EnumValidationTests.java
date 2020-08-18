package com.example.backend.util;

import java.util.Set;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;

import com.example.backend.calendar.Availability;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class EnumValidationTests {
    private Validator validator;

    @BeforeEach
    public void setUp() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Test
    void should_Validate_When_Null() {
        Validated object = new Validated(null, null);
        Set<ConstraintViolation<Validated>> violations = validator.validate(object);

        assertThat(violations).isEmpty();
    }

    @Test
    void should_Not_Validate_When_Not_Matching() {
        Validated object = new Validated(null, "invalid");
        Set<ConstraintViolation<Validated>> violations = validator.validate(object);

        assertThat(violations).hasSize(1);
    }

    @Test
    void should_Not_Validate_When_Lower_Case() {
        Validated object = new Validated(Availability.AVAILABLE.toString().toLowerCase(), null);
        Set<ConstraintViolation<Validated>> violations = validator.validate(object);

        assertThat(violations).hasSize(1);
    }

    @Test
    void should_Validate_When_Valid() {
        Validated object = new Validated(Availability.AVAILABLE.toString(), Availability.AVAILABLE.toString().toLowerCase());
        Set<ConstraintViolation<Validated>> violations = validator.validate(object);

        assertThat(violations).isEmpty();
    }

    private static class Validated {
        @Enum(enumClass = Availability.class)
        private String upperCaseOnly;
        @Enum(enumClass = Availability.class, ignoreCase = true)
        private String caseInsensitive;

        public Validated(String upperCaseOnly, String caseInsensitive) {
            this.upperCaseOnly = upperCaseOnly;
            this.caseInsensitive = caseInsensitive;
        }
    }
}
