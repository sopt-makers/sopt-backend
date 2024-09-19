package org.sopt.app.interfaces.postgres.fortune;

import com.querydsl.jpa.impl.JPAQueryFactory;
import java.time.LocalDate;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.sopt.app.domain.entity.fortune.FortuneWord;
import org.sopt.app.domain.entity.fortune.QFortune;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class FortuneWordRepositoryImpl implements FortuneWordRepository {

    private final JPAQueryFactory queryFactory;

    @Override
    public Optional<FortuneWord> findByRelatedUserIdAndCheckedAt(final Long userId, final LocalDate checkedAt) {
        QFortune fortune = new QFortune("fortune");

        return Optional.ofNullable(
                queryFactory.select(fortune)
                        .from(fortune)
                        .where(fortune.id.eq(1L))
                        .fetchOne()
        );
    }

}
