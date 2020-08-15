package com.example.backend.calendar;

import com.example.backend.util.Date;
import org.springframework.security.core.Authentication;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("api/calendar")
@Validated
public class CalendarController {
    private final CalendarService calendarService;

    public CalendarController(CalendarService calendarService) {
        this.calendarService = calendarService;
    }

    @GetMapping
    public List<DayDto> getCalendar(@RequestParam @Date(pattern = "yyyy-MM-dd", notPast = true) String start,
                                 @RequestParam @Date(pattern = "yyyy-MM-dd", notPast = true) String end){
        return calendarService.getCalendar(start, end);
    }

    @PutMapping
    public void setUserAvailability(@Valid @RequestBody AvailabilityChangeForm form, Authentication authentication){
        calendarService.setUserAvailability(authentication.getName(), form);
    }
}
