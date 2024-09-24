package org.sopt.app.interfaces.postgres.fortune;

import com.querydsl.jpa.impl.JPAQueryFactory;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.sopt.app.domain.entity.fortune.FortuneCard;
import org.sopt.app.domain.entity.fortune.QFortuneCard;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class FortuneCardRepositoryImpl implements FortuneCardRepository {

    private final JPAQueryFactory queryFactory;

    @Override
    public Optional<FortuneCard> findByRelatedUserId(final Long userId) {

        QFortuneCard fortuneCard = QFortuneCard.fortuneCard;

        return Optional.ofNullable(
                queryFactory.select(fortuneCard)
                        .from(fortuneCard)
                        .where(fortuneCard.id.eq(1L))
                        .fetchOne()
        );
    }

}
