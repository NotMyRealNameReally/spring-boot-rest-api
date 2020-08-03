package com.example.backend.calendar;

import org.springframework.data.mongodb.repository.MongoRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface CalendarRepository extends MongoRepository<Day, String> {
    Optional<Day> findByDate(LocalDate date);
    List<Day> findByDateBetween(LocalDate date1, LocalDate date2);
}
