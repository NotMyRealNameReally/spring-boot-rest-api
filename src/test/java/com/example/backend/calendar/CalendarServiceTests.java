package com.example.backend.calendar;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;

import com.example.backend.user.ApplicationUser;
import com.example.backend.user.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.times;

public class CalendarServiceTests {
    @Mock
    private UserRepository userRepository;
    @Mock
    private CalendarRepository calendarRepository;
    @Captor
    private ArgumentCaptor<Day> dayCaptor;
    private CalendarService underTest;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.initMocks(this);
        underTest = new CalendarService(calendarRepository, userRepository);
    }

    @Test
    void should_Get_Calendar() {
        ApplicationUser user1 = generateUser("user1", "1");
        ApplicationUser user2 = generateUser("user2", "2");
        String startDate = "2020-08-15";
        String endDate = "2020-08-16";
        Day day1 = generateEmptyDay(LocalDate.parse(startDate));
        day1.getAvailabilityByUserId().put(user1.getId(), Availability.AVAILABLE);
        given(userRepository.findAll()).willReturn(List.of(user1, user2));
        given(calendarRepository.findByDate(LocalDate.parse(startDate))).willReturn(Optional.of(day1));
        given(calendarRepository.findByDate(LocalDate.parse(endDate))).willReturn(Optional.empty());

        List<DayDto> calendar = underTest.getCalendar(startDate, endDate);

        assertThat(calendar.size()).isEqualTo(2);
        assertThat(calendar).anySatisfy(dayDto -> {
            assertThat(dayDto.getAvailabilityByUsername()).containsEntry(user1.getUsername(), Availability.AVAILABLE);
            assertThat(dayDto.getAvailabilityByUsername()).containsEntry(user2.getUsername(), Availability.UNKNOWN);
            assertThat(dayDto.getEventsById()).containsExactlyElementsOf(day1.getEventsById());
            assertThat(dayDto.getDate()).isEqualTo(day1.getDate());
        });
        assertThat(calendar).anySatisfy(dayDto -> {
            assertThat(dayDto.getAvailabilityByUsername()).containsEntry(user1.getUsername(), Availability.UNKNOWN);
            assertThat(dayDto.getAvailabilityByUsername()).containsEntry(user2.getUsername(), Availability.UNKNOWN);
            assertThat(dayDto.getEventsById()).isEmpty();
            assertThat(dayDto.getDate()).isEqualTo(LocalDate.parse(endDate));
        });
    }

    @Test
    void should_Not_Set_Availability_And_Throw_UsernameNotFoundException_When_Username_Not_Found() {
        String invalidUsername = "user";
        AvailabilityChangeForm form = new AvailabilityChangeForm("2020-08-15", null,
                Availability.AVAILABLE.toString());
        given(userRepository.findByUsername(invalidUsername)).willReturn(Optional.empty());

        assertThatThrownBy(() -> underTest.setUserAvailability(invalidUsername, form))
                .isInstanceOf(UsernameNotFoundException.class);

        then(calendarRepository).shouldHaveNoInteractions();
    }

    @Test
    void should_Remove_User_Entry_When_Availability_Unknown() {
        ApplicationUser user = generateUser("user", "1");
        AvailabilityChangeForm form = new AvailabilityChangeForm("2020-08-15", null,
                Availability.UNKNOWN.toString());
        Day day = generateEmptyDay(LocalDate.parse(form.getStartDate()));
        day.getAvailabilityByUserId().put(user.getId(), Availability.AVAILABLE);
        given(userRepository.findByUsername(user.getUsername())).willReturn(Optional.of(user));
        given(calendarRepository.findByDate(LocalDate.parse(form.getStartDate()))).willReturn(Optional.of(day));

        underTest.setUserAvailability(user.getUsername(), form);

        then(calendarRepository).should().save(dayCaptor.capture());
        Day updatedDay = dayCaptor.getValue();
        assertThat(updatedDay.getAvailabilityByUserId()).doesNotContainKey(user.getId());
    }

    @Test
    void should_Save_User_Entries() {
        ApplicationUser user = generateUser("user", "1");
        AvailabilityChangeForm form = new AvailabilityChangeForm("2020-08-15", "2020-08-16",
                Availability.AVAILABLE.toString());
        given(userRepository.findByUsername(user.getUsername())).willReturn(Optional.of(user));
        given(calendarRepository.findByDate(LocalDate.parse(form.getStartDate()))).willReturn(Optional.empty());

        underTest.setUserAvailability(user.getUsername(), form);

        then(calendarRepository).should(times(2)).save(dayCaptor.capture());
        Day updatedDay = dayCaptor.getValue();
        assertThat(updatedDay.getAvailabilityByUserId()).containsEntry(user.getId(), Availability.AVAILABLE);
    }

    private Day generateEmptyDay(LocalDate date) {
        return new Day(date, new HashMap<>(), new ArrayList<>());
    }

    private ApplicationUser generateUser(String username, String id) {
        ApplicationUser user = new ApplicationUser(username, "password", "abc@gmail.com",
                Collections.emptyList());
        user.setId(id);
        return user;
    }
}
