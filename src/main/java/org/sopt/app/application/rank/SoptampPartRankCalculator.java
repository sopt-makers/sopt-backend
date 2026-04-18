package org.sopt.app.application.rank;

import static java.util.Map.Entry.comparingByValue;
import static java.util.stream.Collectors.groupingBy;
import static java.util.stream.Collectors.summingLong;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Comparator;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import org.sopt.app.application.soptamp.SoptampPointInfo.PartRank;
import org.sopt.app.application.soptamp.SoptampUserInfo;
import org.sopt.app.domain.enums.Part;
import org.sopt.app.domain.enums.SoptPart;

@RequiredArgsConstructor(access = AccessLevel.PUBLIC)
public class SoptampPartRankCalculator {

    private static final int POINT_SCALE = 2;
    private static final RoundingMode POINT_ROUNDING_MODE = RoundingMode.HALF_UP;
    private static final BigDecimal ZERO_POINT = BigDecimal.ZERO.setScale(POINT_SCALE, POINT_ROUNDING_MODE);

    private final List<SoptampUserInfo> userInfos;
    private final Map<SoptPart, Long> partMemberCounts;

    public List<PartRank> calculatePartRank() {
        Map<Part, Long> partScores = calculatePartScores();
        Map<Part, BigDecimal> averagePoints = calculateAveragePoints(partScores);
        Map<Part, Integer> ranks = calculateRanks(averagePoints);

        return Part.getPartsByReturnOrder().stream()
            .map(part -> {
                // TODO: 파트 랭킹 조회시 기존(points) 정수를 유지하고 신규(pointsDecimal)을 추가함으로 앱 하위 호환 대응. 추후 points 제거 필요.
                BigDecimal pointsDecimal = averagePoints.get(part);

                return PartRank.builder()
                    .part(part.getPartName())
                    .rank(ranks.get(part))
                    .points(pointsDecimal.longValue())
                    .pointsDecimal(pointsDecimal)
                    .build();
            })
            .toList();
    }

    private Map<Part, Long> calculatePartScores() {
        Map<Part, Long> partScores = userInfos.stream()
            .filter(userInfo -> SoptPart.toPart(userInfo.getPart()) != null)
            .collect(groupingBy(
                userInfo -> SoptPart.toPart(userInfo.getPart()),
                () -> new EnumMap<>(Part.class),
                summingLong(SoptampUserInfo::getTotalPoints)
            ));

        for (Part part : Part.getAllParts()) {
            partScores.putIfAbsent(part, 0L);
        }

        return partScores;
    }

    private Map<Part, BigDecimal> calculateAveragePoints(Map<Part, Long> partScores) {
        Map<Part, BigDecimal> averagePoints = new EnumMap<>(Part.class);

        for (Part part : Part.getPartsByReturnOrder()) {
            long totalScore = partScores.getOrDefault(part, 0L);
            long memberCount = getMemberCount(part);

            BigDecimal averagePoint = memberCount == 0 ? ZERO_POINT
                : BigDecimal.valueOf(totalScore)
                    .divide(BigDecimal.valueOf(memberCount), POINT_SCALE, POINT_ROUNDING_MODE);

            averagePoints.put(part, averagePoint);
        }

        return averagePoints;
    }

    private long getMemberCount(Part part) {
        return partMemberCounts.getOrDefault(SoptPart.valueOf(part.name()), 0L);
    }

    private Map<Part, Integer> calculateRanks(Map<Part, BigDecimal> averagePoints) {
        List<Entry<Part, BigDecimal>> sortedParts = averagePoints.entrySet().stream()
            .sorted(comparingByValue(Comparator.reverseOrder()))
            .toList();

        Map<Part, Integer> ranks = new EnumMap<>(Part.class);
        BigDecimal previousPoint = null;
        int currentRank = 0;

        for (int i = 0; i < sortedParts.size(); i++) {
            Entry<Part, BigDecimal> entry = sortedParts.get(i);

            if (previousPoint == null || entry.getValue().compareTo(previousPoint) != 0) {
                currentRank = i + 1;
                previousPoint = entry.getValue();
            }

            ranks.put(entry.getKey(), currentRank);
        }

        return ranks;
    }
}
