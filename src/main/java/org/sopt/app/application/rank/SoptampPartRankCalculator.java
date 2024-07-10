package org.sopt.app.application.rank;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.sopt.app.application.soptamp.SoptampPointInfo.PartRank;
import org.sopt.app.common.exception.InternalServerException;
import org.sopt.app.domain.enums.Part;
import org.sopt.app.interfaces.postgres.soptamp_point.SoptampPointRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class SoptampPartRankCalculator {

    private final SoptampPointRepository soptampPointRepository;

    @Value("${sopt.current.generation}")
    private Long currentGeneration;

    private Map<Part, Long> partPoints;

    public List<PartRank> findAllPartRanks() {
        this.partPoints = findSumOfPointAllParts();

        return partPoints.keySet().stream()
                .map(this::createPartRank)
                .toList();
    }

    private Map<Part, Long> findSumOfPointAllParts() {
        List<Part> allParts = Arrays.asList(Part.class.getEnumConstants());
        return allParts.stream()
                .collect(Collectors.toMap(
                        part -> part,
                        part -> soptampPointRepository.findSumOfPointBySamePartAndGeneration(part, currentGeneration))
                );
    }

    private PartRank createPartRank(Part targetPart) {

        return PartRank.builder()
                .part(targetPart.getPartName())
                .rank(getTargetPartRank(targetPart))
                .points(partPoints.get(targetPart))
                .build();
    }

    private int getTargetPartRank(Part targetPart) {
        int rank = 1;

        for (Entry<Part, Long> comparator : partPoints.entrySet()) {
            if (partPoints.get(targetPart).equals(comparator.getValue())) {
                return rank;
            }
            rank++;
        }
        throw new InternalServerException("랭킹 계산 중 오류가 발생했습니다.");
    }
}
