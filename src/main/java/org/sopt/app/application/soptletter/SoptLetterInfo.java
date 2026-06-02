package org.sopt.app.application.soptletter;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class SoptLetterInfo {

    @Getter
    @Builder
    @ToString
    @AllArgsConstructor(access = AccessLevel.PRIVATE)
    public static class Profile {

        private String nickname;
        private boolean isOnboarded;

        public static Profile of(String nickname, boolean isOnboarded) {
            return Profile.builder()
                .nickname(nickname)
                .isOnboarded(isOnboarded)
                .build();
        }
    }
}
