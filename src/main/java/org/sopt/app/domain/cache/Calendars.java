package org.sopt.app.domain.cache;

import java.util.List;
import org.sopt.app.domain.entity.Calendar;

public record Calendars(
        List<Calendar> calendars
) { }
