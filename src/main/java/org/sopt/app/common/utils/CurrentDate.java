package org.sopt.app.common.utils;

import java.time.*;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = lombok.AccessLevel.PRIVATE)
public class CurrentDate {
    public static final LocalDate now = ZonedDateTime.now(ZoneId.of("Asia/Seoul")).toLocalDate();
}
