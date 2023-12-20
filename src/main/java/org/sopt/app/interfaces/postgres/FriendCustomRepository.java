package org.sopt.app.interfaces.postgres;

import java.util.List;
import org.sopt.app.domain.entity.Friend;

public interface FriendCustomRepository {

    List<Long> getFriendRandom(Long userId, int limitNum);
}
