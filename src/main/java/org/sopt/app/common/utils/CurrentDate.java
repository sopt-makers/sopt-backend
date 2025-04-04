package org.sopt.app.common.utils;

import java.time.*;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = lombok.AccessLevel.PRIVATE)
public class CurrentDate {
    public static LocalDate now() {
        return ZonedDateTime.now(ZoneId.of("Asia/Seoul")).toLocalDate();
    }
}
