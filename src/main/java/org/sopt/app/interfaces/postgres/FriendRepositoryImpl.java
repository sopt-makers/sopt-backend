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

    public List<Long> getFriendRandom(Long userId, List<Long> excludeUserIds, int limitNum) {
        QFriend main = new QFriend("main");
        QFriend sub = new QFriend("sub");
        SubQueryExpression<Long> queryOfExcludeIds = getQueryOfExcludeIds(userId);
        return queryFactory
            .select(sub.userId)
            .from(main)
            .join(sub).on(main.friendUserId.eq(sub.userId))
            .where(main.userId.eq(userId)
                .and(sub.friendUserId.ne(userId))
                .and(sub.friendUserId.notIn(queryOfExcludeIds)) // Exclude the friend IDs
                .and(sub.friendUserId.notIn(excludeUserIds))) // Exclude friend who user have to reply first IDs
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
                        // TODO : 요구사항에 따라 찔렀던 안찔렀던, 친구던 친구가 아니던 랜덤 2명씩 보내주도록 결정한 사항 반영
//                        .and(sub.friendUserId.notIn(excludeUserIds)) // Exclude friend who user have to reply first IDs
                )
                .groupBy(sub.userId)
                .having(sub.userId.count().goe(1))
                .orderBy(Expressions.numberTemplate(Long.class, "RANDOM()").asc())
                .limit(2)
                .fetch();
    }

    private SubQueryExpression<Long> getQueryOfExcludeIds(Long userId) {
        QFriend f = new QFriend("f");
        return queryFactory
                .select(f.friendUserId)
                .from(f)
                .where(f.userId.eq(userId));
    }
}
