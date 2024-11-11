package org.sopt.app.presentation.calendar;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import lombok.Getter;
import org.sopt.app.domain.entity.Calendar;
import org.sopt.app.domain.enums.CalendarType;

@Getter
public class RecentCalendarResponse{
        private final String date;
        private final CalendarType type;
        private final String title;

        public static RecentCalendarResponse of(Calendar calendar){
            return new RecentCalendarResponse(
                    calendar.getStartDate(),
                    calendar.getType(),
                    calendar.getTitle()
            );
        }

        public static RecentCalendarResponse createEmptyCalendar(LocalDate date){
            return new RecentCalendarResponse(
                    date,
                    CalendarType.ETC,
                    "일정이 없습니다."
            );
        }

        private RecentCalendarResponse(final LocalDate date, final CalendarType type, final String title){
            this.date = date.format(DateTimeFormatter.ofPattern("MM-dd"));
            this.type = type;
            this.title = title;
        }
}
