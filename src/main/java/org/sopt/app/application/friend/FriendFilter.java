package org.sopt.app.application.friend;

import java.util.List;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import org.sopt.app.application.user.UserInfo.UserProfile;

@RequiredArgsConstructor
public class FriendFilter {
    private final Set<Long> friendUserIds;
    private final Long ownUserId;

    public List<UserProfile> excludeAlreadyFriendUserIds(List<UserProfile> userProfiles) {
        return userProfiles.stream()
                .filter(userProfile -> ownUserId.equals(userProfile.getUserId()))
                .filter(userProfile -> friendUserIds.contains(userProfile.getUserId()))
                .toList();
    }
}
