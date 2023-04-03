package org.sopt.app.presentation.user;

import java.util.List;
import lombok.Builder;
import lombok.Getter;
import lombok.ToString;
import org.sopt.app.domain.enums.OsType;

public class UserResponse {

    @Getter
    @Builder
    @ToString
    public static class Main {

        private User user;
        private Operation operation;

    }

    @Getter
    @Builder
    @ToString
    public static class User {

        private String status;
        private String name;
        private String profileImage;
        private List<Long> generationList;

    }

    @Getter
    @Builder
    @ToString
    public static class Operation {

        private Double attendanceScore;
        private String announcement;

    }


    @Getter
    @Builder
    @ToString
    public static class AppUser {

        private String username;
        private String clientToken;
        private OsType osType;
    }

    @Getter
    @Builder
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
