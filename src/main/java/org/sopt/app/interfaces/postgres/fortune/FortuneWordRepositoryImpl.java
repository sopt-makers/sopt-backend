package org.sopt.app.interfaces.postgres.fortune;

import com.querydsl.jpa.impl.JPAQueryFactory;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.sopt.app.domain.entity.fortune.FortuneWord;
import org.sopt.app.domain.entity.fortune.QFortuneWord;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class FortuneWordRepositoryImpl implements FortuneWordRepository {

    private final JPAQueryFactory queryFactory;
    private final FortuneWordJpaRepository fortuneWordJpaRepository;

    @Override
    public List<Long> findAllIds() {
        QFortuneWord fortuneWord = QFortuneWord.fortuneWord;

        return queryFactory.select(fortuneWord.id)
                .from(fortuneWord)
                .fetch();
    }

    @Override
    public Optional<FortuneWord> findById(Long id) {
        return fortuneWordJpaRepository.findById(id);
    }
}
