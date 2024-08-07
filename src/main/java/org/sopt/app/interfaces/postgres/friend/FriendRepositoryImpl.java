package org.sopt.app.interfaces.postgres.friend;

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
        QFriend subQuery = new QFriend("subQuery");

        SubQueryExpression<Long> subQueryExpression =
            queryFactory
                .select(subQuery.userId)
                .from(subQuery)
                .where(subQuery.friendUserId.eq(userId));

        return queryFactory
            .select(main.friendUserId)
            .from(main)
            .where(main.userId.eq(userId)
                .and(main.friendUserId.in(subQueryExpression))
            )
            .orderBy(Expressions.numberTemplate(Long.class, "RANDOM()").asc())
            .limit(limitNum)
            .fetch();
    }

    @Override
    public List<Long> getFriendRandom(Long userId, List<Long> excludeUserIds, int limitNum) {
        QFriend main = new QFriend("main");

        QFriend subQuery = new QFriend("subQuery");

        SubQueryExpression<Long> subQueryExpression =
            queryFactory
                .select(subQuery.userId)
                .from(subQuery)
                .where(subQuery.friendUserId.eq(userId));

        return queryFactory
            .select(main.friendUserId)
            .from(main)
            .where(main.userId.eq(userId)
                .and(main.friendUserId.in(subQueryExpression))
                .and(main.friendUserId.notIn(excludeUserIds))
            )
            .orderBy(Expressions.numberTemplate(Long.class, "RANDOM()").asc())
            .limit(limitNum)
            .fetch();
    }
}
