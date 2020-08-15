package com.example.backend.calendar;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

public class DayDto {
    private LocalDate date;
    private Map<String, Availability> availabilityByUsername;
    private List<String> eventsById;

    public DayDto(LocalDate date, Map<String, Availability> availabilityByUsername, List<String> eventsById) {
        this.date = date;
        this.availabilityByUsername = availabilityByUsername;
        this.eventsById = eventsById;
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public Map<String, Availability> getAvailabilityByUsername() {
        return availabilityByUsername;
    }

    public void setAvailabilityByUsername(Map<String, Availability> availabilityByUsername) {
        this.availabilityByUsername = availabilityByUsername;
    }

    public List<String> getEventsById() {
        return eventsById;
    }

    public void setEventsById(List<String> eventsById) {
        this.eventsById = eventsById;
    }
}
