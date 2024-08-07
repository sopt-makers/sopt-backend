package org.sopt.app.interfaces.postgres.friend;

import java.util.List;

public interface FriendCustomRepository {

    List<Long> getFriendRandom(Long userId, int limitNum);

    List<Long> getFriendRandom(Long userId, List<Long> excludeUserIds, int limitNum);
}
