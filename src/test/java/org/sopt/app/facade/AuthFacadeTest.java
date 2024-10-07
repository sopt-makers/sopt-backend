package org.sopt.app.facade;

import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.sopt.app.application.auth.JwtTokenService;
import org.sopt.app.application.auth.dto.PlaygroundAuthTokenInfo.AppToken;
import org.sopt.app.application.auth.dto.PlaygroundAuthTokenInfo.RefreshedToken;
import org.sopt.app.application.playground.dto.PlaygroundProfileInfo.PlaygroundMain;
import org.sopt.app.application.playground.PlaygroundAuthService;
import org.sopt.app.application.soptamp.SoptampUserService;
import org.sopt.app.application.user.UserService;
import org.sopt.app.domain.enums.UserStatus;
import org.sopt.app.presentation.auth.AppAuthRequest.AccessTokenRequest;
import org.sopt.app.presentation.auth.AppAuthRequest.CodeRequest;
import org.sopt.app.presentation.auth.AppAuthRequest.RefreshRequest;
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
        PlaygroundMain playgroundMain = PlaygroundMain.builder().name("name").status(UserStatus.ACTIVE).build();
        Long userId = 21L;
        Long soptampUserId = 5L;
        AppToken appToken = AppToken.builder().accessToken("appAccessToken").refreshToken("appRefreshToken").build();

        when(playgroundAuthService.getPlaygroundAccessToken(codeRequest)).thenReturn(accessTokenRequest);
        when(playgroundAuthService.refreshPlaygroundToken(accessTokenRequest)).thenReturn(refreshedToken);
        when(playgroundAuthService.getPlaygroundInfo(refreshedToken.getAccessToken()))
                .thenReturn(playgroundMain);
        when(userService.loginWithUserPlaygroundId(playgroundMain)).thenReturn(userId);
        when(soptampUserService.createSoptampUser(playgroundMain.getName(), userId))
                .thenReturn(soptampUserId);
        when(jwtTokenService.issueNewTokens(userId, playgroundMain)).thenReturn(appToken);

        AppAuthResponse result = authFacade.loginWithPlayground(codeRequest);
        Assertions.assertEquals(appToken.getAccessToken(), result.getAccessToken());
        Assertions.assertEquals(appToken.getRefreshToken(), result.getRefreshToken());
    }

    @Test
    @DisplayName("SUCCESS_인증 회원 토큰 리프레시")
    void SUCCESS_getRefreshToken() {
        RefreshRequest refreshRequest = new RefreshRequest("refreshToken");
        AccessTokenRequest accessTokenRequest = new AccessTokenRequest("accessToken");
        RefreshedToken refreshedToken = RefreshedToken.builder().accessToken("refreshedToken").build();
        PlaygroundMain playgroundMain = PlaygroundMain.builder().accessToken("accessToken").status(UserStatus.ACTIVE).build();
        Long userId = 21L;
        AppToken newAppToken = AppToken.builder().accessToken("newAppAccessToken").refreshToken("newAppRefreshToken")
                .build();

        when(jwtTokenService.getUserIdFromJwtToken(refreshRequest.getRefreshToken())).thenReturn(userId);
        when(userService.getPlaygroundToken(userId)).thenReturn(accessTokenRequest);
        when(playgroundAuthService.refreshPlaygroundToken(accessTokenRequest)).thenReturn(refreshedToken);
        when(playgroundAuthService.getPlaygroundInfo(refreshedToken.getAccessToken()))
                .thenReturn(playgroundMain);
        doNothing().when(userService).updatePlaygroundToken(userId, refreshedToken.getAccessToken());
        when(jwtTokenService.issueNewTokens(userId, playgroundMain)).thenReturn(newAppToken);


        AppAuthResponse result = authFacade.getRefreshToken(refreshRequest);
        Assertions.assertEquals(newAppToken.getAccessToken(), result.getAccessToken());
        Assertions.assertEquals(newAppToken.getRefreshToken(), result.getRefreshToken());
    }
}
