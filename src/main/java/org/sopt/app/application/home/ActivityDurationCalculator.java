package org.sopt.app.application.home;

import java.time.*;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.sopt.app.common.exception.BadRequestException;
import org.sopt.app.common.response.ErrorCode;
import org.sopt.app.common.utils.CurrentDate;

@RequiredArgsConstructor
public class ActivityDurationCalculator {
    private final Long firstActivityGeneration;

    public static ActivityDurationCalculator of(final List<Long> generations) {
        if (generations == null || generations.isEmpty()) {
            throw new BadRequestException(ErrorCode.USER_GENERATION_INFO_NOT_FOUND);
        }

        Long firstGeneration = Long.MAX_VALUE;
        for (Long generation : generations) {
            if (generation < firstGeneration) {
                firstGeneration = generation;
            }
        }
        return new ActivityDurationCalculator(firstGeneration);
    }

    public int getActivityDuration() {
        LocalDate startDate = calculateStartDate();
        return calculateMonthDifference(startDate);
    }

    private LocalDate calculateStartDate() {
        final int SOPT_START_YEAR = 2007;
        final int EVEN_GENERATION_START_MONTH = 3;
        final int ODD_GENERATION_START_MONTH = 9;
        int startMonth = (firstActivityGeneration % 2 == 0) ? EVEN_GENERATION_START_MONTH : ODD_GENERATION_START_MONTH;
        int startYear = SOPT_START_YEAR + (int) (firstActivityGeneration / 2);
        return LocalDate.of(startYear, startMonth, 1);
    }

    private int calculateMonthDifference(LocalDate startDate) {
        Period period = Period.between(startDate, CurrentDate.now);
        int monthDifference = period.getYears() * 12 + period.getMonths();
        return monthDifference + 1;
    }
}
