package org.sopt.app.interfaces.postgres;

import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.sopt.app.domain.entity.SoptampPoint;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class SoptampPointRepositoryImpl implements SoptampPointRepository {
    private final SoptampPointJpaRepository soptampPointJpaRepository;

    @Override
    public List<SoptampPoint> findAllByGeneration(Long currentGeneration) {
        return soptampPointJpaRepository.findAllByGeneration(currentGeneration);
    }

    @Override
    public Optional<SoptampPoint> findBySoptampUserIdAndGeneration(Long soptampUserId, Long currentGeneration) {
        return soptampPointJpaRepository.findBySoptampUserIdAndGeneration(soptampUserId, currentGeneration);
    }

    @Override
    public List<SoptampPoint> findAllBySoptampUserIdInAndGeneration(List<Long> soptampUserIdList,
            Long currentGeneration) {
        return soptampPointJpaRepository.findAllBySoptampUserIdInAndGeneration(soptampUserIdList, currentGeneration);
    }


}
