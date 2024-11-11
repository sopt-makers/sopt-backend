package org.sopt.app.application.calendar;

import java.time.*;
import java.util.*;
import lombok.RequiredArgsConstructor;
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

        return this.getAllCurrentGenerationCalendar().stream()
                .map(CalendarResponse::of)
                .toList();
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

    @Override
    @Transactional
    public RecentCalendarResponse getRecentCalendarResponse() {
        LocalDate now = ZonedDateTime.now(ZoneId.of("Asia/Seoul")).toLocalDate();

        return this.getAllCurrentGenerationCalendar().stream()
                .filter(calendar -> !calendar.getStartDate().isBefore(now))
                .findFirst().map(RecentCalendarResponse::of)
                .orElseGet(() -> RecentCalendarResponse.createEmptyCalendar(now));
    }
}