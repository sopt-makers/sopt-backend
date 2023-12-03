package org.sopt.app.application.friend;

import java.util.List;
import lombok.Builder;
import lombok.Getter;
import org.sopt.app.application.user.UserInfo.PokeProfile;

public class FriendInfo {

    @Getter
    @Builder
    public static class Friend {
        private Long friendId;
        private String friendName;
        private String friendProfileImage;
        private List<PokeProfile> friendList;
    }
}
