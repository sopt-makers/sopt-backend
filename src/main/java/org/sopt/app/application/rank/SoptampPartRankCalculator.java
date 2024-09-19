package org.sopt.app.application.rank;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.sopt.app.application.soptamp.SoptampPointInfo.PartPoint;
import org.sopt.app.application.soptamp.SoptampPointInfo.PartRank;


@RequiredArgsConstructor
public class SoptampPartRankCalculator {

    private final List<PartPoint> partPoints;

    public List<PartRank> findAllPartRanks() {

        return partPoints.stream().map(this::createPartRank).toList();
    }

    private PartRank createPartRank(PartPoint targetPartPoint) {

        return PartRank.builder()
                .part(targetPartPoint.part().getPartName())
                .rank(getTargetPartRank(targetPartPoint))
                .points(targetPartPoint.points())
                .build();
    }

    private int getTargetPartRank(PartPoint targetPartPoint) {
        int rankPoint = 1;

        for (PartPoint comparisonPartPoint : partPoints) {
            if (targetPartPoint.points() < comparisonPartPoint.points()) {
                rankPoint++;
            }
        }
        return rankPoint;
    }
}
