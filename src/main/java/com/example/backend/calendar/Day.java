package com.example.backend.calendar;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document("calendar")
public class Day {

    @Id
    private String id;
    private LocalDate date;
    private final Map<String, Availability> userAvailability; // User id as key.
    private final List<String> eventsById;

    public Day(LocalDate date, Map<String, Availability> userAvailability, List<String> eventsById) {
        this.date = date;
        this.userAvailability = userAvailability;
        this.eventsById = eventsById;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public LocalDate getDate() {
        return date;
    }

    public Map<String, Availability> getUserAvailability() {
        return userAvailability;
    }

    public List<String> getEventsById() {
        return eventsById;
    }
}
