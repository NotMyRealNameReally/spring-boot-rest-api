package com.example.backend.calendar;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import java.util.stream.Collectors;

import com.example.backend.user.ApplicationUser;
import com.example.backend.user.UserRepository;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class CalendarService {
    private final CalendarRepository calendarRepository;
    private final UserRepository userRepository;

    public CalendarService(CalendarRepository calendarRepository, UserRepository userRepository) {
        this.calendarRepository = calendarRepository;
        this.userRepository = userRepository;
    }

    public List<DayDto> getCalendar(String start, String end) {
        LocalDate startDate = LocalDate.parse(start);
        LocalDate endDate = LocalDate.parse(end);

        return startDate.datesUntil(endDate.plusDays(1))
                        .map(date -> {
                            Day day = calendarRepository
                                    .findByDate(date)
                                    .orElseGet(() -> generateEmptyDay(date));
                            return convertToDayDto(day);
                        })
                        .collect(Collectors.toList());
    }

    public void setUserAvailability(String username, AvailabilityChangeForm form) {
        ApplicationUser user = userRepository
                .findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));
        Availability availability = Availability.valueOf(form.getAvailability().toUpperCase());
        LocalDate startDate = LocalDate.parse(form.getStartDate());
        LocalDate endDate = LocalDate.parse(form.getEndDate().orElseGet(form::getStartDate));

        Consumer<Day> updateDay;
        if (availability == Availability.UNKNOWN) {
            updateDay = day -> day.getAvailabilityByUserId().remove(user.getId());
        } else {
            updateDay = day -> day.getAvailabilityByUserId().put(user.getId(), availability);
        }
        startDate.datesUntil(endDate.plusDays(1)).forEach(date -> {
            Day day = calendarRepository
                    .findByDate(date)
                    .orElseGet(() -> generateEmptyDay(date));
            updateDay.accept(day);
            calendarRepository.save(day);
        });
    }

    @Scheduled(cron = "0 0 12 * * *")
    private void removePastDates() {
        calendarRepository.deleteByDateBefore(LocalDate.now());
    }

    private DayDto convertToDayDto(Day day) {
        Map<String, Availability> availabilityByUserId = day.getAvailabilityByUserId();
        Map<String, Availability> availabilityByUsername = new HashMap<>();

        userRepository.findAll().forEach(user ->
                availabilityByUsername.put(user.getUsername(),
                        availabilityByUserId.getOrDefault(user.getId(), Availability.UNKNOWN)));
        return new DayDto(day.getDate(), availabilityByUsername, day.getEventsById());
    }

    private Day generateEmptyDay(LocalDate date) {
        return new Day(date, new HashMap<>(), new ArrayList<>());
    }
}
