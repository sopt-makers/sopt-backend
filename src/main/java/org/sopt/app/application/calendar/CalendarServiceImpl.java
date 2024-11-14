package org.sopt.app.application.calendar;

import java.util.*;
import lombok.RequiredArgsConstructor;
import org.sopt.app.common.utils.CurrentDate;
import org.sopt.app.domain.cache.*;
import org.sopt.app.domain.entity.Calendar;
import org.sopt.app.interfaces.postgres.CalendarRepository;
import org.sopt.app.interfaces.postgres.redis.CachedCalendarRepository;
import org.sopt.app.presentation.calendar.CalendarResponse;
import org.sopt.app.presentation.calendar.RecentCalendarResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class CalendarServiceImpl implements CalendarService {

    private final CalendarRepository calendarRepository;
    private final CachedCalendarRepository cachedCalendarRepository;

    @Value("${sopt.current.generation}")
    private Integer currentGeneration;

    @Override
    @Transactional
    public List<CalendarResponse> getAllCurrentGenerationCalendarResponse() {
        List<Calendar> calendars = this.getAllCurrentGenerationCalendar();
        Optional<Calendar> recentCalendar = this.getRecentCalendar(calendars);
        return recentCalendar.map(value -> calendars.stream()
                .map(calendar -> CalendarResponse.of(calendar, calendar.getId().equals(value.getId())))
                .toList())
                .orElseGet(() -> this.getAllCurrentGenerationCalendar().stream()
                .map(calendar -> CalendarResponse.of(calendar, false))
                .toList());
    }

    private List<Calendar> getAllCurrentGenerationCalendar() {
        Optional<CachedAllCalendarResponse> cachedCalendar = cachedCalendarRepository.findById(currentGeneration);

        if (cachedCalendar.isPresent()) {
            return cachedCalendar.get().getCalendars().calendars();
        }

        return this.cacheAllCalendarResponse();
    }

    private List<Calendar> cacheAllCalendarResponse() {
        List<Calendar> calendars = calendarRepository.findAllByGenerationOrderByStartDate(currentGeneration);
        cachedCalendarRepository.save(new CachedAllCalendarResponse(currentGeneration, new Calendars(calendars)));
        return calendars;
    }

    private Optional<Calendar> getRecentCalendar(List<Calendar> calendars) {
        return calendars.stream()
                .filter(calendar -> !calendar.getStartDate().isBefore(CurrentDate.now))
                .findFirst();
    }

    @Override
    @Transactional
    public RecentCalendarResponse getRecentCalendarResponse() {
        List<Calendar> calendars = this.getAllCurrentGenerationCalendar();
        return this.getRecentCalendar(calendars).map(RecentCalendarResponse::of)
                .orElseGet(() -> RecentCalendarResponse.of(calendars.getLast()));
    }
}