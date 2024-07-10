package org.sopt.app.interfaces.postgres;

import java.util.List;
import java.util.Optional;
import org.sopt.app.domain.entity.SoptampPoint;

public interface SoptampPointRepository {

    List<SoptampPoint> findAllByGeneration(Long currentGeneration);

    Optional<SoptampPoint> findBySoptampUserIdAndGeneration(Long soptampUserId, Long currentGeneration);

    List<SoptampPoint> findAllBySoptampUserIdInAndGeneration(List<Long> soptampUserIdList, Long currentGeneration);
}
