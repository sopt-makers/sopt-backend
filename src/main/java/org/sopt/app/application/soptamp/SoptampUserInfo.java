package org.sopt.app.application.soptamp;

import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

public class SoptampUserInfo {

    @Getter
    @Builder
    @ToString
    public static class SoptampUser {

        private Long id;
        private Long userId;
        private String profileMessage;
        private Long totalPoints;
        private String nickname;

        public static SoptampUser of(Long id, Long userId, String profileMessage, Long totalPoints, String nickname) {
            return new SoptampUser(id, userId, profileMessage, totalPoints, nickname);
        }
    }

    @Getter
    @Builder
    @ToString
    public static class SoptampUserPlaygroundInfo {

        private Long userId;
        private Long playgroundId;
        private String name;
        private Long generation;
        private String part;
    }

}
