package org.sopt.app.interfaces.postgres.soptamp_point;

import java.util.List;
import java.util.Optional;
import org.sopt.app.domain.entity.SoptampPoint;
import org.sopt.app.domain.enums.Part;

public interface SoptampPointRepository {

    SoptampPoint save(SoptampPoint soptampPoint);

    List<SoptampPoint> findAllByGeneration(Long currentGeneration);

    Optional<SoptampPoint> findBySoptampUserIdAndGeneration(Long soptampUserId, Long currentGeneration);

    List<SoptampPoint> findAllBySoptampUserIdInAndGeneration(List<Long> soptampUserIdList, Long currentGeneration);

    List<SoptampPoint> findAllBySoptampUserPartAndGeneration(Part part, Long currentGeneration);
}
