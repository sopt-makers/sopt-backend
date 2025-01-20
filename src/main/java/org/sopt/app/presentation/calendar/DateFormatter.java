package org.sopt.app.presentation.calendar;

import java.time.format.DateTimeFormatter;
import java.util.Locale;
import lombok.NoArgsConstructor;
import org.sopt.app.domain.entity.Calendar;

@NoArgsConstructor(access = lombok.AccessLevel.PRIVATE)
public class DateFormatter {

    public static String formatDateRange(Calendar calendar) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("M월 d일 (E)", Locale.KOREAN);

        if (calendar.getIsOneDaySchedule()) {
            return calendar.getStartDate().format(formatter);
        }
        return calendar.getStartDate().format(formatter) + " ~ " + calendar.getEndDate().format(formatter);
    }
}