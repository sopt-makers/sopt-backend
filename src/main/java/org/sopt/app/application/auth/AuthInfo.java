package org.sopt.app.application.auth;

import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

public class AuthInfo {

    @Getter
    @Builder
    @ToString
    public static class PlaygroundAccessToken {

        private String accessToken;
    }
}
