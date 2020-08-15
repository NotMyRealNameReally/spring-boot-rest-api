package com.example.backend.calendar;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@Document("calendar")
public class Day {

    @Id
    private String id;
    private LocalDate date;
    private final Map<String, Availability> availabilityByUserId;
    private final List<String> eventsById;

    public Day(LocalDate date, Map<String, Availability> availabilityByUserId, List<String> eventsById) {
        this.date = date;
        this.availabilityByUserId = availabilityByUserId;
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

    public Map<String, Availability> getAvailabilityByUserId() {
        return availabilityByUserId;
    }

    public List<String> getEventsById() {
        return eventsById;
    }
}
