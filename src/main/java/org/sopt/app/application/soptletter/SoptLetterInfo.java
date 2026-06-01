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
    public static class Nickname {

        private String nickname;

        public static Nickname of(String nickname) {
            return Nickname.builder()
                .nickname(nickname)
                .build();
        }
    }
}
