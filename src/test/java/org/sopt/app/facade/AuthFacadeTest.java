package org.sopt.app.facade;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;

import java.util.List;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.sopt.app.application.auth.JwtTokenService;
import org.sopt.app.application.auth.dto.PlaygroundAuthTokenInfo.*;
import org.sopt.app.application.playground.dto.PlaygroundProfileInfo.ActivityCardinalInfo;
import org.sopt.app.application.playground.dto.PlaygroundProfileInfo.PlaygroundMain;
import org.sopt.app.application.playground.PlaygroundAuthService;
import org.sopt.app.application.playground.dto.PlaygroundProfileInfo.PlaygroundProfile;
import org.sopt.app.application.soptamp.SoptampUserService;
import org.sopt.app.application.user.UserService;
import org.sopt.app.common.fixtures.SoptampUserFixture;
import org.sopt.app.common.fixtures.UserFixture;
import org.sopt.app.domain.enums.UserStatus;
import org.sopt.app.presentation.auth.AppAuthRequest.*;
import org.sopt.app.presentation.auth.AppAuthResponse;

@ExtendWith(MockitoExtension.class)
class AuthFacadeTest {

    @Mock
    private JwtTokenService jwtTokenService;
    @Mock
    private UserService userService;
    @Mock
    private PlaygroundAuthService playgroundAuthService;
    @Mock
    private SoptampUserService soptampUserService;

    @InjectMocks
    private AuthFacade authFacade;

    @Test
    @DisplayName("SUCCESS_인증 회원 플그로 로그인")
    void SUCCESS_loginWithPlaygroundActive() {
        CodeRequest codeRequest = new CodeRequest("code");
        AccessTokenRequest accessTokenRequest = new AccessTokenRequest("accessToken");
        RefreshedToken refreshedToken = RefreshedToken.builder().accessToken("refreshedToken").build();
        Long userId = SoptampUserFixture.SOPTAMP_USER_1.getUserId();
        Long playgroundId = UserFixture.myPlaygroundId;
        AppToken appToken = AppToken.builder().accessToken("appAccessToken").refreshToken("appRefreshToken").build();
        when(playgroundAuthService.getPlaygroundMember(refreshedToken.getAccessToken()))
                .thenReturn(PlaygroundMain.builder().id(playgroundId).hasProfile(true).build());
        when(playgroundAuthService.getPlaygroundAccessToken(codeRequest)).thenReturn(accessTokenRequest);
        when(playgroundAuthService.refreshPlaygroundToken(accessTokenRequest)).thenReturn(refreshedToken);
        when(playgroundAuthService.getPlaygroundMemberProfile(refreshedToken.getAccessToken(), playgroundId))
                .thenReturn(PlaygroundProfile.builder().activities(List.of(new ActivityCardinalInfo("35,서버"))).build());
        when(userService.upsertUser(any())).thenReturn(userId);
        when(jwtTokenService.issueNewTokens(userId, playgroundId)).thenReturn(appToken);
        when(playgroundAuthService.getStatus(any())).thenReturn(UserStatus.ACTIVE);

        AppAuthResponse result = authFacade.loginWithPlayground(codeRequest);
        Assertions.assertEquals(appToken.getAccessToken(), result.getAccessToken());
        Assertions.assertEquals(appToken.getRefreshToken(), result.getRefreshToken());
        Assertions.assertEquals(refreshedToken.getAccessToken(), result.getPlaygroundToken());
        Assertions.assertEquals(UserStatus.ACTIVE, result.getStatus());
    }

    @Test
    @DisplayName("SUCCESS_인증 회원 토큰 리프레시")
    void SUCCESS_getRefreshToken() {
        String refreshToken =  "refreshToken";
        AccessTokenRequest accessTokenRequest = new AccessTokenRequest("accessToken");
        RefreshedToken refreshedToken = RefreshedToken.builder().accessToken("refreshedToken").build();

        Long userId = UserFixture.myAppUserId;
        Long playgroundId = UserFixture.myPlaygroundId;
        AppToken newAppToken = AppToken.builder().accessToken("newAppAccessToken").refreshToken("newAppRefreshToken")
                .build();

        when(jwtTokenService.getUserIdFromJwtToken(refreshToken)).thenReturn(userId);
        when(userService.getPlaygroundToken(userId)).thenReturn(accessTokenRequest);
        when(playgroundAuthService.refreshPlaygroundToken(accessTokenRequest)).thenReturn(refreshedToken);
        when(playgroundAuthService.getPlaygroundMember(refreshedToken.getAccessToken())).thenReturn(PlaygroundMain.builder().id(playgroundId).build());
        doNothing().when(userService).updatePlaygroundToken(userId, refreshedToken.getAccessToken());
        when(jwtTokenService.issueNewTokens(userId, playgroundId)).thenReturn(newAppToken);

        AppAuthResponse result = authFacade.getRefreshToken(refreshToken);
        Assertions.assertEquals(newAppToken.getAccessToken(), result.getAccessToken());
        Assertions.assertEquals(newAppToken.getRefreshToken(), result.getRefreshToken());
    }
}
