package org.sopt.app.application.soptamp;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.val;
import org.sopt.app.application.soptamp.SoptampPointInfo.Point;
import org.sopt.app.domain.entity.SoptampPoint;
import org.sopt.app.domain.entity.SoptampUser;
import org.sopt.app.domain.enums.Part;
import org.sopt.app.domain.enums.UserStatus;
import org.sopt.app.interfaces.postgres.soptamp_point.SoptampPointRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class SoptampPointService {

    private final SoptampPointRepository soptampPointRepository;

    @Value("${sopt.current.generation}")
    private Long currentGeneration;

    public List<Point> findCurrentGenerationPoints() {
        return soptampPointRepository.findAllByGeneration(currentGeneration).stream()
                .map(Point::of)
                .toList();
    }

    public List<Point> findCurrentPointListBySoptampUserIds(List<Long> soptampUserIdList) {
        return soptampPointRepository.findAllBySoptampUserIdInAndGeneration(soptampUserIdList, currentGeneration)
                .stream()
                .map(Point::of)
                .toList();
    }

    @Transactional
    public void addPoint(Long soptampUserId, Integer level) {
        val soptampPoint = soptampPointRepository.findBySoptampUserIdAndGeneration(soptampUserId, currentGeneration);
        soptampPoint.ifPresent(point -> point.addPointsByLevelValue(level));
    }

    @Transactional
    public void subtractPoint(Long soptampUserId, Integer level) {
        val soptampPoint = soptampPointRepository.findBySoptampUserIdAndGeneration(soptampUserId, currentGeneration);
        soptampPoint.ifPresent(point -> point.subtractPointsByLevelValue(level));
    }

    @Transactional
    public void upsertSoptampPoint(UserStatus status, Long soptampUserId) {
        if (status.equals(UserStatus.INACTIVE)) {
            return;
        }
        val soptampPoint = soptampPointRepository.findBySoptampUserIdAndGeneration(soptampUserId, currentGeneration);

        if (soptampPoint.isEmpty()) {
            soptampPointRepository.save(SoptampPoint.createNewSoptampPoint(currentGeneration, soptampUserId));
        }
    }

    @Transactional
    public void initPoint(Long soptampUserId) {
        val soptampPoint = soptampPointRepository.findBySoptampUserIdAndGeneration(soptampUserId, currentGeneration);
        soptampPoint.ifPresent(SoptampPoint::initPoint);
    }

    public void deleteAll() {
        soptampPointRepository.deleteAll();
    }

    public List<SoptampPoint> createCurrentGenerationSoptampPointList(
            List<SoptampUser> soptampUserList
    ) {
        val soptampUserIdList = soptampUserList.stream().map(SoptampUser::getId).toList();

        val prevSoptampUserIdList = soptampPointRepository.findAllBySoptampUserIdInAndGeneration(
                soptampUserIdList, currentGeneration
        ).stream().map(SoptampPoint::getSoptampUserId).toList();

        val newSoptampUserList = soptampUserList.stream()
                .map(soptampUser -> prevSoptampUserIdList.contains(soptampUser.getId()) ? null : soptampUser)
                .filter(Objects::nonNull)
                .toList();

        val soptampPointList = newSoptampUserList.stream().map(soptampUser ->
                SoptampPoint.createNewSoptampPoint(currentGeneration, soptampUser.getId())
        ).toList();

        soptampPointRepository.saveAll(soptampPointList);
        return soptampPointList;
    }

    public Map<Part, Long> findSumOfPointAllParts() {
        List<Part> allParts = Part.getAllParts();
        return allParts.stream()
                .collect(Collectors.toMap(
                        part -> part,
                        part -> soptampPointRepository.findSumOfPointBySamePartAndGeneration(part, currentGeneration))
                );
    }
}
