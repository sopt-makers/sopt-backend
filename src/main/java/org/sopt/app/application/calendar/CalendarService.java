package org.sopt.app.application.calendar;

import java.util.List;
import org.sopt.app.presentation.calendar.CalendarResponse;
import org.sopt.app.presentation.calendar.RecentCalendarResponse;

public interface CalendarService {
    List<CalendarResponse> getAllCurrentGenerationCalendarResponse();
    RecentCalendarResponse getRecentCalendarResponse();
}
