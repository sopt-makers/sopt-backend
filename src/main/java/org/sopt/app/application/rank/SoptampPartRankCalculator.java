package org.sopt.app.application.rank;

import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import lombok.RequiredArgsConstructor;
import org.sopt.app.application.soptamp.SoptampPointInfo.PartRank;
import org.sopt.app.common.exception.InternalServerException;
import org.sopt.app.domain.enums.Part;


@RequiredArgsConstructor
public class SoptampPartRankCalculator {

    private final Map<Part, Long> partPoints;
    private int rankPoint = 1;

    public List<PartRank> findAllPartRanks() {

        return partPoints.keySet().stream()
                .map(this::createPartRank)
                .toList();
    }

    private PartRank createPartRank(Part targetPart) {

        return PartRank.builder()
                .part(targetPart.getPartName())
                .rank(getTargetPartRank(targetPart))
                .points(partPoints.get(targetPart))
                .build();
    }

    private int getTargetPartRank(Part targetPart) {

        for (Entry<Part, Long> comparator : partPoints.entrySet()) {
            if (partPoints.get(targetPart).equals(comparator.getValue())) {
                return rankPoint;
            }
            rankPoint++;
        }
        throw new InternalServerException("랭킹 계산 중 오류가 발생했습니다.");
    }
}
