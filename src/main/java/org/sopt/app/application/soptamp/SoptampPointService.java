package org.sopt.app.application.soptamp;

import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.val;
import org.sopt.app.application.soptamp.SoptampPointInfo.Point;
import org.sopt.app.common.exception.BadRequestException;
import org.sopt.app.domain.entity.SoptampPoint;
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

    @Transactional
    public void addPoint(Long soptampUserId, Integer level) {
        val soptampPoint = soptampPointRepository.findAllBySoptampUserIdAndGeneration(soptampUserId, currentGeneration);
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
        val soptampPoint = soptampPointRepository.findAllBySoptampUserIdAndGeneration(soptampUserId, currentGeneration).orElseThrow(
            () -> new BadRequestException("해당 유저의 포인트가 존재하지 않습니다.")
        );
        val newSoptampPoint = SoptampPoint.builder()
            .id(soptampPoint.getId())
            .generation(soptampPoint.getGeneration())
            .soptampUserId(soptampPoint.getSoptampUserId())
            .points(soptampPoint.getPoints() - level)
            .build();
        soptampPointRepository.save(newSoptampPoint);
    }

    @Transactional
    public void upsertSoptampPoint(UserStatus status, Long soptampUserId) {
        val soptampPoint = soptampPointRepository.findAllBySoptampUserIdAndGeneration(soptampUserId, currentGeneration);
        if (status.equals(UserStatus.INACTIVE) || soptampPoint.isPresent()) {
            return;
        }
        val newSoptampPoint = SoptampPoint.builder()
            .generation(currentGeneration)
            .soptampUserId(soptampUserId)
            .points(0L)
            .build();
        soptampPointRepository.save(newSoptampPoint);
    }
}
