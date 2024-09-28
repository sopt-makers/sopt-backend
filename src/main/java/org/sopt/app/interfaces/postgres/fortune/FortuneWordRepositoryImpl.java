package org.sopt.app.interfaces.postgres.fortune;

import com.querydsl.jpa.impl.JPAQueryFactory;
import java.time.LocalDate;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.sopt.app.domain.entity.fortune.FortuneWord;
import org.sopt.app.domain.entity.fortune.QFortuneWord;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public abstract class FortuneWordRepositoryImpl implements FortuneWordRepository {

    private final JPAQueryFactory queryFactory;

    @Override
    public Optional<FortuneWord> findByUserIdAndCheckAt(final Long userId, final LocalDate checkedAt) {
        QFortuneWord fortuneWord = QFortuneWord.fortuneWord;

        return Optional.ofNullable(
                queryFactory.select(fortuneWord)
                        .from(fortuneWord)
                        .where(
                                fortuneWord.userId.eq(userId)
                                        .and(fortuneWord.checkAt.eq(checkedAt))
                        )
                        .fetchOne()
        );
    }
}