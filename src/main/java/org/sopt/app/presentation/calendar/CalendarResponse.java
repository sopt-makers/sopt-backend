package org.sopt.app.presentation.calendar;

import java.time.LocalDate;
import lombok.*;
import org.sopt.app.domain.entity.Calendar;

@Getter
@Builder
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class CalendarResponse {
    private final String title;
    private final LocalDate startDate;
    private final LocalDate endDate;
    private final Boolean isOneDaySchedule;
    private final Boolean isOnlyActiveGeneration;
    private final Boolean isRecentSchedule;

    public static CalendarResponse of(Calendar calendar, Boolean isRecentSchedule) {
        return CalendarResponse.builder()
                .startDate(calendar.getStartDate())
                .endDate(calendar.getEndDate())
                .title(calendar.getTitle())
                .isOneDaySchedule(calendar.getIsOneDaySchedule())
                .isOnlyActiveGeneration(calendar.getIsOnlyActiveGeneration())
                .isRecentSchedule(isRecentSchedule)
                .build();
    }
}
