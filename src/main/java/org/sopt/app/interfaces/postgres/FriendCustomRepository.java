package org.sopt.app.interfaces.postgres;

import java.util.List;

public interface FriendCustomRepository {

    List<Long> getFriendRandom(Long userId, int limitNum);
}