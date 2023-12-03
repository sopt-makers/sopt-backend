package org.sopt.app.interfaces.postgres;

import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.jpa.impl.JPAQueryFactory;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.val;
import org.sopt.app.domain.entity.QFriend;
import org.sopt.app.domain.entity.QUser;
import org.sopt.app.domain.entity.User;

@RequiredArgsConstructor
public class UserRepositoryImpl implements UserCustomRepository{
    private final JPAQueryFactory queryFactory;

    @Override
    public List<User> findRandomFriendsOfFriends(Long userId, Long friendId, int limitNum) {
        val au = QUser.user;
        val f = QFriend.friend;
        val sf = new QFriend("sf");

        return queryFactory
            .selectFrom(au)
            .join(f).on(f.friendUserId.eq(au.id))
            .leftJoin(sf).on(sf.friendUserId.eq(au.id).and(sf.userId.eq(userId)))
            .where(f.userId.eq(friendId)
                .and(au.id.ne(userId))
                .and(sf.userId.isNull())) // Ensuring that left join found no match
            .orderBy(Expressions.numberTemplate(Long.class, "RANDOM()").asc())
            .limit(2)
            .fetch();
    }
}
