package org.sopt.app.application.rank;

import static org.assertj.core.api.Assertions.assertThat;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import org.assertj.core.groups.Tuple;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.sopt.app.application.soptamp.SoptampPointInfo.PartRank;
import org.sopt.app.application.soptamp.SoptampUserInfo;
import org.sopt.app.domain.enums.Part;
import org.sopt.app.domain.enums.SoptPart;

class SoptampPartRankCalculatorTest {

    @ParameterizedTest
    @CsvSource({
        "1000, 25, 200.00",
        "900, 9, 300.00",
        "100, 2, 70.71",
        "0, 10, 0.00",
        "500, 0, 0.00"
    })
    @DisplayName("SUCCESS_파트 점수는 총점을 인원의 제곱근으로 나눈 값이며 인원이 0명이면 0점임")
    void SUCCESS_calculatePartRank_adjustedPoints(long totalPoints, long memberCount, BigDecimal expected) {
        // given
        SoptampPartRankCalculator calculator = new SoptampPartRankCalculator(
            List.of(userInfo(SoptPart.SERVER, totalPoints)),
            Map.of(SoptPart.SERVER, memberCount)
        );

        // when
        PartRank serverRank = calculator.calculatePartRank().stream()
            .filter(partRank -> partRank.getPart().equals(Part.SERVER.getPartName()))
            .findFirst()
            .orElseThrow();

        // then
        assertThat(serverRank.getPointsDecimal()).isEqualTo(expected);
    }

    @Test
    @DisplayName("SUCCESS_파트 순위는 1인 평균이 아닌 총점을 인원의 제곱근으로 나눈 값 기준으로 정렬됨")
    void SUCCESS_calculatePartRank_sortedByAdjustedPoints() {
        // given
        SoptampPartRankCalculator calculator = new SoptampPartRankCalculator(
            List.of(
                userInfo(SoptPart.PLAN, 1100L),
                userInfo(SoptPart.DESIGN, 1500L),
                userInfo(SoptPart.SERVER, 2880L)
            ),
            Map.of(
                SoptPart.PLAN, 11L,
                SoptPart.DESIGN, 16L,
                SoptPart.SERVER, 32L
            )
        );

        // when
        List<PartRank> result = calculator.calculatePartRank();

        // then
        assertThat(result)
            .extracting(PartRank::getPart, PartRank::getRank, PartRank::getPointsDecimal)
            .containsExactly(
                Tuple.tuple(Part.PLAN.getPartName(), 3, new BigDecimal("331.66")),
                Tuple.tuple(Part.DESIGN.getPartName(), 2, new BigDecimal("375.00")),
                Tuple.tuple(Part.WEB.getPartName(), 4, new BigDecimal("0.00")),
                Tuple.tuple(Part.IOS.getPartName(), 4, new BigDecimal("0.00")),
                Tuple.tuple(Part.ANDROID.getPartName(), 4, new BigDecimal("0.00")),
                Tuple.tuple(Part.SERVER.getPartName(), 1, new BigDecimal("509.12"))
            );
    }

    @Test
    @DisplayName("SUCCESS_인원이 다른 파트라도 점수가 같으면 공동 순위이고 다음 순위는 동점 파트 수를 건너뜀")
    void SUCCESS_calculatePartRank_whenTiedAcrossDifferentMemberCounts() {
        // given
        SoptampPartRankCalculator calculator = new SoptampPartRankCalculator(
            List.of(
                userInfo(SoptPart.PLAN, 200L),
                userInfo(SoptPart.DESIGN, 300L),
                userInfo(SoptPart.SERVER, 320L)
            ),
            Map.of(
                SoptPart.PLAN, 4L,
                SoptPart.DESIGN, 9L,
                SoptPart.SERVER, 16L
            )
        );

        // when
        List<PartRank> result = calculator.calculatePartRank();

        // then
        assertThat(result)
            .extracting(PartRank::getPart, PartRank::getRank, PartRank::getPointsDecimal)
            .containsExactly(
                Tuple.tuple(Part.PLAN.getPartName(), 1, new BigDecimal("100.00")),
                Tuple.tuple(Part.DESIGN.getPartName(), 1, new BigDecimal("100.00")),
                Tuple.tuple(Part.WEB.getPartName(), 4, new BigDecimal("0.00")),
                Tuple.tuple(Part.IOS.getPartName(), 4, new BigDecimal("0.00")),
                Tuple.tuple(Part.ANDROID.getPartName(), 4, new BigDecimal("0.00")),
                Tuple.tuple(Part.SERVER.getPartName(), 3, new BigDecimal("80.00"))
            );
    }

    private SoptampUserInfo userInfo(SoptPart part, long totalPoints) {
        return SoptampUserInfo.builder()
            .part(part)
            .totalPoints(totalPoints)
            .build();
    }
}
