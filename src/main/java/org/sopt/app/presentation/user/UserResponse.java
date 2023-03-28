package org.sopt.app.presentation.user;

import javax.persistence.Column;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
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

        @Column(nullable = false)
        public String username;

        @Column
        private String clientToken;

        @Column
        @Enumerated(EnumType.STRING)
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
