package org.sopt.app.presentation.calendar;

import java.util.List;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class AllCalendarResponse {
    private final List<CalendarResponse> calendars;
}
