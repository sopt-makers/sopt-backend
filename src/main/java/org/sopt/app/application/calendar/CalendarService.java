package org.sopt.app.application.calendar;

import java.util.List;
import org.sopt.app.presentation.calendar.CalendarResponse;

public interface CalendarService {
    List<CalendarResponse> getAllCurrentGenerationCalendar();
}
