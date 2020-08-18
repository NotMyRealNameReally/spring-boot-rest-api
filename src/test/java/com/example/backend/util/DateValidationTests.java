package com.example.backend.util;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Set;
import java.util.function.Predicate;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class DateValidationTests {
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
    void should_Not_Validate_When_Pattern_Not_Matching() {
        Validated object = new Validated("2137-APR-20", null);
        Set<ConstraintViolation<Validated>> violations = validator.validate(object);

        assertThat(violations)
                .anyMatch(havingMessageAndPropertyPath("Should be of pattern: " + Validated.pattern,
                        "date"));
    }

    @Test
    void should_Not_Validate_When_Date_In_Past() {
        Validated object = new Validated(null, "2010-01-01");
        Set<ConstraintViolation<Validated>> violations = validator.validate(object);

        assertThat(violations)
                .anyMatch(havingMessageAndPropertyPath("Cannot be in the past.", "dateNotPast"));
    }

    @Test
    void should_Validate_When_All_Valid() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(Validated.pattern);
        Validated object = new Validated("2020-08-17", LocalDate.now().format(formatter));
        Set<ConstraintViolation<Validated>> violations = validator.validate(object);

        assertThat(violations).isEmpty();
    }

    public static <T> Predicate<ConstraintViolation<T>> havingMessageAndPropertyPath(String message, String propertyPath) {
        return l -> message.equals(l.getMessage()) &&
                propertyPath.equals(l.getPropertyPath().toString());
    }

    private static class Validated {
        public static final String pattern = "yyyy-MM-dd";
        @Date(pattern = pattern)
        private String date;
        @Date(pattern = pattern, notPast = true)
        private String dateNotPast;

        public Validated(String date, String dateNotPast) {
            this.date = date;
            this.dateNotPast = dateNotPast;
        }
    }
}



