package org.sopt.app.presentation.auth;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

public class AuthResponse {

    @Getter
    @Setter
    @ToString
    public static class PlaygroundMemberResponse {

        private Long id;
        private String name;
        private Long generation;
        private String profileImage;
        private Boolean hasProfile;
    }

    @Getter
    @Setter
    @ToString
    public static class LoginResponse {

        private Long userId;
        private String profileMessage;
    }

    @Getter
    @Setter
    @ToString
    public static class Token {

        @Schema(description = "앱 서버 AccessToken", example = "eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiIyMiIsImV4cCI6MTY4MDAxNDQzNn0.asdfasdfasdfasdfasdfasdf")
        private String accessToken;
        @Schema(description = "앱 서버 RefreshToken", example = "eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiIyMiIsImV4cCI6MTY4MDAxNDQzNn0.asdfasdfasdfasdfasdfasdf")
        private String refreshToken;
    }
}
