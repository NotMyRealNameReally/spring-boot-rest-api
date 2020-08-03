package com.example.backend.calendar;

import com.example.backend.util.Enum;

import javax.validation.constraints.NotNull;
import java.util.Optional;

public class AvailabilityChangeForm {
    @NotNull
    private String startDate;
    private String endDate;
    @Enum(enumClass = Availability.class, ignoreCase = true)
    private String availability;

    public AvailabilityChangeForm(String startDate, String endDate, String availability) {
        this.startDate = startDate;
        this.endDate = endDate;
        this.availability = availability;
    }

    public String getStartDate() {
        return startDate;
    }

    public Optional<String> getEndDate() {
        return Optional.ofNullable(endDate);
    }

    public String getAvailability() {
        return availability;
    }
}
