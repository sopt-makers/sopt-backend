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
}
