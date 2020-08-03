package com.example.backend.calendar;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

public class DayDto {
    private LocalDate date;
    private Map<String, Availability> userAvailability; // Username as key
    private List<String> eventsById;

    public DayDto(LocalDate date, Map<String, Availability> userAvailability, List<String> eventsById) {
        this.date = date;
        this.userAvailability = userAvailability;
        this.eventsById = eventsById;
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public Map<String, Availability> getUserAvailability() {
        return userAvailability;
    }

    public void setUserAvailability(Map<String, Availability> userAvailability) {
        this.userAvailability = userAvailability;
    }

    public List<String> getEventsById() {
        return eventsById;
    }

    public void setEventsById(List<String> eventsById) {
        this.eventsById = eventsById;
    }
}
