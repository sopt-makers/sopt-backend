package org.sopt.app.application.rank;

import static java.util.Map.Entry.comparingByValue;

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

    private static final BigDecimal ZERO_POINT = BigDecimal.ZERO.setScale(2, RoundingMode.HALF_UP);

    private final List<SoptampUserInfo> userInfos;
    private final Map<SoptPart, Long> partMemberCounts;

    public List<PartRank> calculatePartRank() {
        PartScores partScores = new PartScores();
        userInfos.forEach(userInfo -> addPartScore(userInfo, partScores));

        Map<Part, BigDecimal> averagePoints = calculateAveragePoints(partScores);
        Map<Part, Integer> ranks = calculateRanks(averagePoints);

        return Part.getPartsByReturnOrder().stream()
            .map(part -> PartRank.builder()
                .part(part.getPartName())
                .rank(ranks.get(part))
                .points(averagePoints.get(part))
                .build())
            .toList();
    }

    private void addPartScore(SoptampUserInfo userInfo, PartScores partScores) {
        Part part = SoptPart.toPart(userInfo.getPart());
        if (part == null) {
            return;
        }
        partScores.addPartScore(part, userInfo.getTotalPoints());
    }

    private Map<Part, BigDecimal> calculateAveragePoints(PartScores partScores) {
        Map<Part, BigDecimal> averagePoints = new EnumMap<>(Part.class);

        for (Part part : Part.getPartsByReturnOrder()) {
            long totalScore = partScores.getPoints(part);
            long memberCount = partMemberCounts.getOrDefault(SoptPart.valueOf(part.name()), 0L);

            BigDecimal averagePoint = memberCount == 0 ? ZERO_POINT
                : BigDecimal.valueOf(totalScore)
                    .divide(BigDecimal.valueOf(memberCount), 2, RoundingMode.HALF_UP);

            averagePoints.put(part, averagePoint);
        }

        return averagePoints;
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
