package org.sopt.app.application.friend;

import java.util.Set;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class FriendFilter {
    private final Set<Long> friendIds;

    public Set<Long> excludeAlreadyFriendIds(Set<Long> userIds) {
        userIds.removeAll(friendIds);
        return userIds;
    }
}
