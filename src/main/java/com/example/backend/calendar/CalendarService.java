package com.example.backend.calendar;

import com.example.backend.user.ApplicationUser;
import com.example.backend.user.UserRepository;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class CalendarService {
    private final CalendarRepository calendarRepository;
    private final UserRepository userRepository;

    public CalendarService(CalendarRepository calendarRepository, UserRepository userRepository) {
        this.calendarRepository = calendarRepository;
        this.userRepository = userRepository;
    }

    public List<Day> getCalendar() {
        LocalDate today = LocalDate.now();
        return calendarRepository.findAll().stream()
                .filter(day -> day.getDate().compareTo(today) >= 0)
                .collect(Collectors.toList());
    }

    public void setUserAvailability(String name, AvailabilityChangeForm form) {
        ApplicationUser user = userRepository
                .findByUsername(name)
                .orElseThrow(()-> new UsernameNotFoundException("User not found"));
        Day day = calendarRepository
                .findByDate(LocalDate.parse(form.getStartDate()))
                .orElse(getNewDay(form.getStartDate()));

        day.getUserAvailability().put(user.getId(), Availability.valueOf(form.getAvailability().toUpperCase()));
        calendarRepository.save(day);
    }

    private Day getNewDay(String date) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.mm.yyyy");
        return new Day(LocalDate.parse(date), new HashMap<>(), new ArrayList<>());
    }
}
