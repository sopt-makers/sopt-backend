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
        QFriend sub = new QFriend("sub");
        QFriend f = new QFriend("f");

        SubQueryExpression<Long> excludedFriendIds = queryFactory
                .select(f.friendUserId)
                .from(f)
                .where(f.userId.eq(userId));

        return queryFactory
            .select(sub.userId)
            .from(main)
            .join(sub).on(main.friendUserId.eq(sub.userId))
            .where(main.userId.eq(userId)
                .and(sub.friendUserId.ne(userId))
                    .and(sub.friendUserId.notIn(excludedFriendIds))) // Exclude the friend IDs
            .groupBy(sub.userId)
            .having(sub.userId.count().goe(1))
            .orderBy(Expressions.numberTemplate(Long.class, "RANDOM()").asc())
            .limit(2)
            .fetch();
    }

    public List<Long> getFriendRandomIncludeDuplicated(Long userId, List<Long> excludeUserIds, int limitNum) {
        QFriend main = new QFriend("main");
        QFriend sub = new QFriend("sub");
        return queryFactory
                .select(sub.userId)
                .from(main)
                .join(sub).on(main.friendUserId.eq(sub.userId))
                .where(main.userId.eq(userId)
                        .and(sub.friendUserId.ne(userId))
                )
                .groupBy(sub.userId)
                .having(sub.userId.count().goe(1))
                .orderBy(Expressions.numberTemplate(Long.class, "RANDOM()").asc())
                .limit(2)
                .fetch();
    }
}
