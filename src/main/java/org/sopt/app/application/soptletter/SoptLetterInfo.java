package org.sopt.app.application.soptletter;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.sopt.app.domain.entity.soptletter.SoptLetterProfile;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class SoptLetterInfo {

    @Getter
    @Builder
    @ToString
    @AllArgsConstructor(access = AccessLevel.PRIVATE)
    public static class Profile {

        private String nickname;
        private boolean isOnboarded;

        public static Profile from(SoptLetterProfile profile) {
            return Profile.builder()
                .nickname(profile.getNickname())
                .isOnboarded(profile.isOnboarded())
                .build();
        }
    }
}
