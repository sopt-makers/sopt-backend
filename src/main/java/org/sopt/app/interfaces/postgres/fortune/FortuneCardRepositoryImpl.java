package org.sopt.app.interfaces.postgres.fortune;

import com.querydsl.jpa.impl.JPAQueryFactory;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.sopt.app.domain.entity.fortune.FortuneCard;
import org.sopt.app.domain.entity.fortune.QFortuneCard;
import org.sopt.app.domain.entity.fortune.QFortuneWord;
import org.sopt.app.domain.entity.fortune.QUserFortune;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class FortuneCardRepositoryImpl implements FortuneCardRepository {

    private final JPAQueryFactory queryFactory;

    @Override
    public Optional<FortuneCard> findByRelatedUserId(final Long userId) {
        QUserFortune userFortune = QUserFortune.userFortune;
        QFortuneCard fortuneCard = QFortuneCard.fortuneCard;
        QFortuneWord fortuneWord = QFortuneWord.fortuneWord;

        return Optional.ofNullable(
                queryFactory.select(fortuneCard)
                        .from(userFortune)
                        .join(fortuneWord)
                        .on(userFortune.fortuneId.eq(fortuneWord.id))
                        .join(fortuneCard)
                        .on(fortuneWord.fortuneCardId.eq(fortuneCard.id))
                        .where(userFortune.userId.eq(userId))
                        .fetchOne()
        );
    }

}
