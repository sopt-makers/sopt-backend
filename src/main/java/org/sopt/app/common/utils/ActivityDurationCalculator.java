package org.sopt.app.common.utils;

import java.time.*;
import java.util.Comparator;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.sopt.app.common.exception.BadRequestException;
import org.sopt.app.common.response.ErrorCode;

@RequiredArgsConstructor
public final class ActivityDurationCalculator {

    private static final int SOPT_START_YEAR = 2007;
    private static final int EVEN_GENERATION_START_MONTH = 3;
    private static final int ODD_GENERATION_START_MONTH = 9;

    public static int calculate(List<Long> generations) {
        if (generations == null || generations.isEmpty()) {
            throw new BadRequestException(ErrorCode.USER_GENERATION_INFO_NOT_FOUND);
        }

        Long firstGeneration = generations.stream()
            .min(Comparator.naturalOrder())
            .orElseThrow(() -> new BadRequestException(ErrorCode.USER_GENERATION_INFO_NOT_FOUND));

        LocalDate startDate = getGenerationStartDate(firstGeneration);
        return getMonthDifferenceFromNow(startDate);
    }

    private static LocalDate getGenerationStartDate(Long generation) {
        int startMonth = (generation % 2 == 0) ? EVEN_GENERATION_START_MONTH : ODD_GENERATION_START_MONTH;
        int startYear = SOPT_START_YEAR + (int) (generation / 2);
        return LocalDate.of(startYear, startMonth, 1);
    }

    private static int getMonthDifferenceFromNow(LocalDate startDate) {
        Period period = Period.between(startDate, CurrentDate.now);
        return period.getYears() * 12 + period.getMonths() + 1;
    }
}
