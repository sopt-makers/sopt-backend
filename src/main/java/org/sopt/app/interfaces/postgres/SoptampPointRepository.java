package org.sopt.app.interfaces.postgres;

import java.util.List;
import java.util.Optional;
import org.sopt.app.domain.entity.SoptampPoint;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SoptampPointRepository extends JpaRepository<SoptampPoint, Long> {

    List<SoptampPoint> findAllByGeneration(Long currentGeneration);

    Optional<SoptampPoint> findAllBySoptampUserIdAndGeneration(Long soptampUserId, Long currentGeneration);

    List<SoptampPoint> findAllBySoptampUserIdInAndGeneration(List<Long> soptampUserIdList, Long currentGeneration);
}
