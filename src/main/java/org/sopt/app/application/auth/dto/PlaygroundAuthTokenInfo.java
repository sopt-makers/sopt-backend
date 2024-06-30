package org.sopt.app.application.auth.dto;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class PlaygroundAuthTokenInfo {

    @Getter
    @Builder
    public static class AppToken {

        private String accessToken;
        private String refreshToken;
    }

    @Getter
    @Builder
    @NoArgsConstructor(access = AccessLevel.PRIVATE)
    @AllArgsConstructor(access = AccessLevel.PRIVATE)
    public static class RefreshedToken {

        private String accessToken;
        private String errorCode;
    }
}
