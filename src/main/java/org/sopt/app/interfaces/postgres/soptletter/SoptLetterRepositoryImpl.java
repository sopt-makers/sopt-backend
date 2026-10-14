package org.sopt.app.interfaces.postgres.soptletter;

import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.sopt.app.domain.entity.soptletter.QSoptLetter;
import org.sopt.app.domain.entity.soptletter.QSoptLetterTopic;
import org.sopt.app.domain.entity.soptletter.QSoptLetterTopicRead;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class SoptLetterRepositoryImpl implements SoptLetterRepositoryCustom {

    private final JPAQueryFactory queryFactory;

    @Override
    public List<TopicUnreadCount> countUnreadLettersByTopic(Long userId, Long profileId) {
        QSoptLetter letter = QSoptLetter.soptLetter;
        QSoptLetterTopic topic = QSoptLetterTopic.soptLetterTopic;
        QSoptLetterTopicRead read = QSoptLetterTopicRead.soptLetterTopicRead;

        return queryFactory.select(Projections.constructor(TopicUnreadCount.class, letter.topicId, letter.count()))
                .from(letter)
                .join(topic).on(topic.id.eq(letter.topicId))
                .leftJoin(read).on(read.topicId.eq(letter.topicId), read.userId.eq(userId))
                .where(
                        read.id.isNull().or(letter.id.gt(read.lastReadLetterId)),
                        notWrittenBy(letter, profileId))
                .groupBy(letter.topicId)
                .fetch();
    }

    private BooleanExpression notWrittenBy(QSoptLetter letter, Long profileId) {
        if (profileId == null) {
            return null;
        }
        return letter.authorProfileId.isNull().or(letter.authorProfileId.ne(profileId));
    }
}
