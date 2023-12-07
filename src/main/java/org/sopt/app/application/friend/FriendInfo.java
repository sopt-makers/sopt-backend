package org.sopt.app.application.friend;

import java.util.List;
import lombok.Builder;
import lombok.Getter;
import org.sopt.app.application.user.UserInfo.PokeProfile;

public class FriendInfo {

    @Getter
    @Builder
    public static class Friend {
        private Long id;
        private String name;
        private String profileImage;
        private List<PokeProfile> friendList;
    }
}
