package org.sopt.app.application.friend;

import java.util.Set;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class FriendFilter {
    private final Set<Long> friendIds;
    private final Long userId;

    public Set<Long> excludeAlreadyFriendIds(Set<Long> userIds) {
        userIds.removeAll(friendIds);
        userIds.remove(userId);
        return userIds;
    }
}
