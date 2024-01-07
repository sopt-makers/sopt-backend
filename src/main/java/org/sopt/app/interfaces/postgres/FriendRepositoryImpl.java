package org.sopt.app.interfaces.postgres;

import com.querydsl.core.types.SubQueryExpression;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.jpa.impl.JPAQueryFactory;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.sopt.app.domain.entity.QFriend;

@RequiredArgsConstructor
public class FriendRepositoryImpl implements FriendCustomRepository {

    private final JPAQueryFactory queryFactory;

    public List<Long> getFriendRandom(Long userId, int limitNum) {
        QFriend main = new QFriend("main");

        return queryFactory
            .select(main.friendUserId)
            .from(main)
            .where(main.userId.eq(userId))
            .orderBy(Expressions.numberTemplate(Long.class, "RANDOM()").asc())
            .limit(limitNum)
            .fetch();
    }

    @Override
    public List<Long> getFriendRandom(Long userId, List<Long> excludeUserIds, int limitNum) {
        QFriend main = new QFriend("main");

        return queryFactory
            .select(main.friendUserId)
            .from(main)
            .where(main.userId.eq(userId)
                .and(main.friendUserId.notIn(excludeUserIds))
            )
            .orderBy(Expressions.numberTemplate(Long.class, "RANDOM()").asc())
            .limit(limitNum)
            .fetch();
    }
}
