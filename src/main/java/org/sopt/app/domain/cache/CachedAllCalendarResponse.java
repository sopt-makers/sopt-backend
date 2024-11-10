package org.sopt.app.domain.cache;

import lombok.*;
import org.sopt.app.presentation.calendar.AllCalendarResponse;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;

@Getter
@AllArgsConstructor(access = AccessLevel.PUBLIC)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@RedisHash(value = "allCalendar", timeToLive = 60 * 60 * 24 * 7L)
public class CachedAllCalendarResponse {

    @Id
    private Integer generation;

    private AllCalendarResponse calendars;
}
