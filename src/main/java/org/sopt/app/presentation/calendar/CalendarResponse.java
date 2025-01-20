package org.sopt.app.presentation.calendar;

import lombok.*;
import org.sopt.app.domain.entity.Calendar;
import org.sopt.app.domain.enums.CalendarType;

@Getter
@Builder
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class CalendarResponse {
    private final String date;
    private final String title;
    private final CalendarType type;
    private final Boolean isRecentSchedule;

    public static CalendarResponse of(Calendar calendar, String date, Boolean isRecentSchedule) {
        return CalendarResponse.builder()
                .title(calendar.getTitle())
                .type(calendar.getType())
                .date(date)
                .isRecentSchedule(isRecentSchedule)
                .build();
    }
}
