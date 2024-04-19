package org.sopt.app.application.soptamp;

import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.val;
import org.sopt.app.application.soptamp.SoptampPointInfo.PartRank;
import org.sopt.app.application.soptamp.SoptampPointInfo.Point;
import org.sopt.app.domain.entity.SoptampPoint;
import org.sopt.app.domain.enums.Part;
import org.sopt.app.domain.enums.UserStatus;
import org.sopt.app.interfaces.postgres.SoptampPointRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class SoptampPointService {

    private final SoptampPointRepository soptampPointRepository;

    @Value("${sopt.current.generation}")
    private Long currentGeneration;

    public List<Point> findCurrentPointList() {
        return soptampPointRepository.findAllByGeneration(currentGeneration).stream()
            .map(point ->
                SoptampPointInfo.Point.of(
                    point.getId(),
                    point.getGeneration(),
                    point.getSoptampUserId(),
                    point.getPoints()
                )
            ).toList();
    }

    public List<Point> findCurrentPointListBySoptampUserIds(List<Long> soptampUserIdList) {

        return soptampPointRepository.findAllBySoptampUserIdInAndGeneration(soptampUserIdList, currentGeneration).stream()
                .map(point ->
                        SoptampPointInfo.Point.of(
                                point.getId(),
                                point.getGeneration(),
                                point.getSoptampUserId(),
                                point.getPoints()
                        )
                ).toList();
    }

    @Transactional
    public void addPoint(Long soptampUserId, Integer level) {
        val soptampPoint = soptampPointRepository.findBySoptampUserIdAndGeneration(soptampUserId, currentGeneration);
        if(soptampPoint.isPresent()){
            val soptampPointEntity = soptampPoint.get();
            val newSoptampPoint = SoptampPoint.builder()
                .id(soptampPointEntity.getId())
                .generation(soptampPointEntity.getGeneration())
                .soptampUserId(soptampPointEntity.getSoptampUserId())
                .points(soptampPointEntity.getPoints() + level)
                .build();
            soptampPointRepository.save(newSoptampPoint);
        }
    }

    @Transactional
    public void subtractPoint(Long soptampUserId, Integer level) {
        val soptampPoint = soptampPointRepository.findBySoptampUserIdAndGeneration(soptampUserId, currentGeneration);
        if(soptampPoint.isEmpty()){
            return;
        }
        val soptampPointEntity = soptampPoint.get();
        val newSoptampPoint = SoptampPoint.builder()
            .id(soptampPointEntity.getId())
            .generation(soptampPointEntity.getGeneration())
            .soptampUserId(soptampPointEntity.getSoptampUserId())
            .points(soptampPointEntity.getPoints() - level)
            .build();
        soptampPointRepository.save(newSoptampPoint);
    }

    @Transactional
    public void upsertSoptampPoint(UserStatus status, Long soptampUserId) {
        if (status.equals(UserStatus.INACTIVE)) {
            return;
        }
        val soptampPoint = soptampPointRepository.findBySoptampUserIdAndGeneration(soptampUserId, currentGeneration);
        if (soptampPoint.isPresent()) {
            return;
        }
        val newSoptampPoint = SoptampPoint.builder()
            .generation(currentGeneration)
            .soptampUserId(soptampUserId)
            .points(0L)
            .build();
        soptampPointRepository.save(newSoptampPoint);
    }

    @Transactional
    public void initPoint(Long soptampUserId) {
        val soptampPoint = soptampPointRepository.findBySoptampUserIdAndGeneration(soptampUserId, currentGeneration);
        if(soptampPoint.isEmpty()){
            return;
        }
        val soptampPointEntity = soptampPoint.get();
        val newSoptampPoint = SoptampPoint.builder()
                .id(soptampPointEntity.getId())
                .generation(soptampPointEntity.getGeneration())
                .soptampUserId(soptampPointEntity.getSoptampUserId())
                .points(0L)
                .build();
        soptampPointRepository.save(newSoptampPoint);
    }

    public Map<Part, PartRank> findPartRanks(Map<Part, Long> partPoints) {
        return partPoints.entrySet().stream()
                .collect(Collectors.toMap(
                        Entry::getKey,
                        entry -> calculatePartRank(entry.getKey(), partPoints)
                ));
    }

    private PartRank calculatePartRank(Part part, Map<Part, Long> partPoints) {
        Integer rank = 1;

        for (Entry<Part, Long> comparator : partPoints.entrySet()) {
            if(partPoints.get(part) < comparator.getValue()){
                rank++;
            }
        }

        return PartRank.builder()
                .part(part.getPartName())
                .rank(rank)
                .points(partPoints.get(part))
                .build();
    }


    public Long calculateSumOfPoints(List<Point> soptampPointList) {
        return soptampPointList.stream()
            .map(Point::getPoints)
            .reduce(0L, Long::sum);
    }
}
