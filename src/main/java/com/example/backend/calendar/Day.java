package com.example.backend.calendar;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document
public class Day {

    @Id
    private final LocalDate date;
    private final Map<String, Availability> availabilities; // User id as key.
    private final List<String> events; // Event id

    public Day(LocalDate date, Map<String, Availability> availability, List<String> events) {
        this.date = date;
        this.availabilities = availability;
        this.events = events;
    }

    public LocalDate getDate() {
        return date;
    }

    public Map<String, Availability> getAvailabilities() {
        return availabilities;
    }

    public List<String> getEvents() {
        return events;
    }
}
