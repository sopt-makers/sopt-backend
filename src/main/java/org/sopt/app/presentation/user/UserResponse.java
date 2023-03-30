package org.sopt.app.presentation.user;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.sopt.app.domain.enums.OsType;

public class UserResponse {

    @Getter
    @Setter
    @ToString
    public static class Main {

        public String username;
        private String clientToken;
        private OsType osType;
    }

    @Getter
    @Setter
    @ToString
    public static class Soptamp {

        private String nickname;
        private Long points;
        private String profileMessage;
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
}
