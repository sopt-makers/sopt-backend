package org.sopt.app.presentation.auth;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

public class AuthRequest {

    @Getter
    @Setter
    @ToString
    public static class CodeRequest {

        @Schema(description = "플레이그라운드 OAuth Token", example = "eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiIyMiIsImV4cCI6MTY4MDAxNDQzNn0.asdfasdfasdfasdfasdfasdf")
        private String code;
    }

    @Getter
    @Builder
    @ToString
    public static class AccessTokenRequest {

        private String accessToken;
    }

    @Getter
    @Setter
    @ToString
    public static class RefreshRequest {

        private String refreshToken;
    }
}
