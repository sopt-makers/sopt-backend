package org.sopt.app.application.calendar;

import lombok.RequiredArgsConstructor;
import org.sopt.app.interfaces.CalendarRepository;
import org.sopt.app.presentation.calendar.AllCalendarResponse;
import org.sopt.app.presentation.calendar.CalendarResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CalendarServiceImpl implements CalendarService {

    private final CalendarRepository calendarRepository;

    @Value("${sopt.current.generation}")
    private Integer currentGeneration;

    @Override
    public AllCalendarResponse getAllCurrentGenerationCalendar() {

        return new AllCalendarResponse(
                calendarRepository.findAllByGenerationOrderByStartDate(currentGeneration)
                        .stream().map(CalendarResponse::of)
                        .toList()
        );
    }
}
