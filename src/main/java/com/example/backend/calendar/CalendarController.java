package com.example.backend.calendar;

import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("api/calendar")
public class CalendarController {
    private final CalendarService calendarService;

    public CalendarController(CalendarService calendarService) {
        this.calendarService = calendarService;
    }

    @GetMapping
    public List<Day> getCalendar(Pageable pageable){
        return calendarService.getCalendar();
    }

    @PutMapping
    public void setUserAvailability(@Valid @RequestBody AvailabilityChangeForm form, Authentication authentication){
        calendarService.setUserAvailability(authentication.getName(), form);
    }
}
