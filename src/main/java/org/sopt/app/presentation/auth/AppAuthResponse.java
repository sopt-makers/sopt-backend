package org.sopt.app.presentation.auth;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;
import org.sopt.app.domain.enums.UserStatus;

@Builder
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class AppAuthResponse {
    @Schema(description = "앱 서버 AccessToken", example = "eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiIyMiIsImV4cCI6MTY4MDAxNDQzNn0.asdfasdfasdfasdfasdfasdf")
    private String accessToken;
    @Schema(description = "앱 서버 RefreshToken", example = "eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiIyMiIsImV4cCI6MTY4MDAxNDQzNn0.asdfasdfasdfasdfasdfasdf")
    private String refreshToken;
    @Schema(description = "플레이그라운드 AccessToken", example = "eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiIyMiIsImV4cCI6MTY4MDAxNDQzNn0.asdfasdfasdfasdfasdfasdf")
    private String playgroundToken;
    @Schema(description = "활동/비활동/비회원 분기 처리", example = "ACTIVE")
    private UserStatus status;
}
