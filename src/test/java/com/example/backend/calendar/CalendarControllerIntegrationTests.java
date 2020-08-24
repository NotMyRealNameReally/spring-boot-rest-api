package com.example.backend.calendar;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.util.Collections;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class CalendarControllerIntegrationTests {
    @Autowired
    MockMvc mockMvc;
    @MockBean
    CalendarService calendarService;

    @ParameterizedTest
    @CsvSource({"2137-APR-20, 2137-04-20, start: Should be of pattern: yyyy-MM-dd",
            "2137-04-20, 2137-APR-20, end: Should be of pattern: yyyy-MM-dd"})
    @WithMockUser
    void should_Respond_BAD_REQUEST_When_Date_Wrong_Pattern(String start, String end,
                                                            String expectedMessage) throws Exception {
        MvcResult result = mockMvc.perform(
                get("/api/calendar")
                        .param("start", start)
                        .param("end", end))
                .andExpect(status().isBadRequest())
                .andReturn();
        String resultBody = result.getResponse().getContentAsString();
        assertThat(resultBody).contains(expectedMessage);
        then(calendarService).shouldHaveNoInteractions();
    }

    @ParameterizedTest
    @CsvSource({"2000-01-01, 2137-04-20, start: Cannot be in the past",
            "2137-04-20, 2000-01-01, end: Cannot be in the past"})
    @WithMockUser
    void should_Respond_BAD_REQUEST_When_Date_In_Past(String start, String end,
                                                      String expectedMessage) throws Exception {
        MvcResult result = mockMvc.perform(
                get("/api/calendar")
                        .param("start", start)
                        .param("end", end))
                .andExpect(status().isBadRequest())
                .andReturn();
        String resultBody = result.getResponse().getContentAsString();
        assertThat(resultBody).contains(expectedMessage);
        then(calendarService).shouldHaveNoInteractions();
    }

    @Test
    @WithMockUser
    void should_Respond_OK_When_Date_Valid() throws Exception {
        String validDate = "2137-04-20";
        given(calendarService.getCalendar(validDate, validDate)).willReturn(Collections.emptyList());

        mockMvc.perform(
                get("/api/calendar")
                        .param("start", validDate)
                        .param("end", validDate))
                .andExpect(status().isOk());
        then(calendarService).should().getCalendar(validDate, validDate);
    }

    @ParameterizedTest
    @CsvSource({"1998-11-11, 2137-04-20, available, startDate: Cannot be in the past",
            "2137-04-20, 1998-11-11, available, endDate: Cannot be in the past",
            "2137-04-20, 2137-04-21, invalid, availability: Can only be one of:"})
    @WithMockUser
    void should_Respond_BAD_REQUEST_When_Form_Invalid(String start, String end, String availability,
                                                      String expectedMessage) throws Exception {
        AvailabilityChangeForm form = new AvailabilityChangeForm(start, end, availability);
        ObjectMapper mapper = new ObjectMapper();
        mapper.registerModule(new Jdk8Module());
        String requestBody = mapper.writeValueAsString(form);

        MvcResult result = mockMvc.perform(
                put("/api/calendar")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isBadRequest())
                .andReturn();
        String resultBody = result.getResponse().getContentAsString();
        assertThat(resultBody).contains(expectedMessage);
        then(calendarService).shouldHaveNoInteractions();
    }

    @Test
    @WithMockUser
    void should_Respond_OK_When_Form_Valid() throws Exception {
        AvailabilityChangeForm form = new AvailabilityChangeForm("2137-04-20", "2137-04-21",
                "available");
        ObjectMapper mapper = new ObjectMapper();
        mapper.registerModule(new Jdk8Module());
        String requestBody = mapper.writeValueAsString(form);

        mockMvc.perform(
                put("/api/calendar")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isOk());
        then(calendarService).should().setUserAvailability(anyString(), any(AvailabilityChangeForm.class));
    }
}
