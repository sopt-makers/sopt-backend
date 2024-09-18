package org.sopt.app.interfaces.postgres.soptamp_point;

import com.querydsl.jpa.impl.JPAQueryFactory;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.sopt.app.domain.entity.QSoptampPoint;
import org.sopt.app.domain.entity.QSoptampUser;
import org.sopt.app.domain.entity.soptamp.SoptampPoint;
import org.sopt.app.domain.enums.Part;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class SoptampPointRepositoryImpl implements SoptampPointRepository {

    private final SoptampPointJpaRepository soptampPointJpaRepository;
    private final JPAQueryFactory queryFactory;

    @Override
    public SoptampPoint save(SoptampPoint soptampPoint) {
        return soptampPointJpaRepository.save(soptampPoint);
    }

    @Override
    public void saveAll(List<SoptampPoint> soptampPoints) {
        soptampPointJpaRepository.saveAll(soptampPoints);
    }

    @Override
    public void deleteAll() {
        soptampPointJpaRepository.deleteAll();
    }

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

    @Override
    public Long findSumOfPointBySamePartAndGeneration(Part part, Long generation) {
        QSoptampPoint soptampPoint = new QSoptampPoint("soptampPoint");
        QSoptampUser soptampUser = new QSoptampUser("soptampUser");

        return Optional.ofNullable(
                queryFactory
                .select(soptampPoint.points.sum())
                .from(soptampUser)
                .join(soptampPoint)
                .on(soptampPoint.soptampUserId.eq(soptampUser.id)
                        .and(soptampUser.part.eq(String.valueOf(part))
                                .and(soptampUser.generation.eq(generation))))
                . fetchOne())
                .orElse(0L);
    }


}
