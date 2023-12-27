package org.sopt.app.interfaces.postgres;

import java.util.List;
import org.sopt.app.domain.entity.User;

public interface UserCustomRepository {

    List<User> findRandomFriendsOfFriendsExclude(Long userId, Long friendId, List<Long> excludeIds, int limitNum);
    List<User> findRandomFriendsOfFriends(Long userId, Long friendId, int limitNum);
}
