package org.sopt.app.application.user;

import lombok.*;

public class UserInfo {

    @Getter
    @Builder
    @ToString
    public static class Id {
        private Long id;
    }

    @Getter
    @Builder
    @ToString
    public static class Nickname {

        private String nickname;
    }

    @Getter
    @Builder
    @ToString
    public static class ProfileMessage {

        private String profileMessage;
    }

    @Getter
    @Builder
    @ToString
    public static class UserProfile {
        private Long userId;
        private String name;
        private Long playgroundId;
    }
}
