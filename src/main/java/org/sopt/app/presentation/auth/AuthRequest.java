package org.sopt.app.presentation.auth;

import io.swagger.v3.oas.annotations.media.Schema;
import javax.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

public class AuthRequest {

    @Getter
    @Setter
    @ToString
    public static class CodeRequest {

        @Schema(description = "플레이그라운드 OAuth Token", example = "eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiIyMiIsImV4cCI6MTY4MDAxNDQzNn0.asdfasdfasdfasdfasdfasdf")
        @NotNull(message = "code may not be null")
        private String code;
    }

    @Getter
    @Setter
    @ToString
    public static class AccessTokenRequest {

        private String accessToken;
    }

    @Getter
    @Setter
    @ToString
    public static class RefreshRequest {

        @Schema(description = "앱 Refresh Token", example = "eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiIyMiIsImV4cCI6MTY4MDAxNDQzNn0.asdfasdfasdfasdfasdfasdf")
        @NotNull(message = "code may not be null")
        private String refreshToken;
    }
}
