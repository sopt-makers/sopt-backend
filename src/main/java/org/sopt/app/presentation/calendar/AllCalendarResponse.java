package org.sopt.app.presentation.calendar;

import java.util.List;

public record AllCalendarResponse(
        List<CalendarResponse> calendars
) { }
