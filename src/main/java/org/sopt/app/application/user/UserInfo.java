package org.sopt.app.application.user;

import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

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
    public static class Token {
        private String accessToken;
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
    public static class IsOptIn {
        private Boolean isOptIn;
    }

    @Getter
    @Builder
    @ToString
    public static class UserProfile {
        private Long userId;
        private String name;
        private Long playgroundId;
    }

    @Getter
    @Builder
    @ToString
    public static class PokeProfile {
        private Long userId;
        private String name;
        private String profileImage;
        private Long generation;
        private String part;
        private Boolean isAlreadyPoked = false;
    }
}
