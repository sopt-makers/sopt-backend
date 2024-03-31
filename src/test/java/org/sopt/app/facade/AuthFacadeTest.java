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
import org.sopt.app.application.auth.PlaygroundAuthInfo.AppToken;
import org.sopt.app.application.auth.PlaygroundAuthInfo.PlaygroundMain;
import org.sopt.app.application.auth.PlaygroundAuthInfo.RefreshedToken;
import org.sopt.app.application.auth.PlaygroundAuthService;
import org.sopt.app.application.soptamp.SoptampPointService;
import org.sopt.app.application.soptamp.SoptampUserService;
import org.sopt.app.application.user.UserInfo.Id;
import org.sopt.app.application.user.UserService;
import org.sopt.app.domain.enums.UserStatus;
import org.sopt.app.presentation.auth.AppAuthRequest.AccessTokenRequest;
import org.sopt.app.presentation.auth.AppAuthRequest.CodeRequest;
import org.sopt.app.presentation.auth.AppAuthRequest.RefreshRequest;
import org.sopt.app.presentation.auth.AppAuthResponse.Token;
import org.sopt.app.presentation.auth.AppAuthResponseMapper;

@ExtendWith(MockitoExtension.class)
public class AuthFacadeTest {

    @Mock
    private AppAuthResponseMapper appAuthResponseMapper;
    @Mock
    private JwtTokenService jwtTokenService;
    @Mock
    private UserService userService;
    @Mock
    private PlaygroundAuthService playgroundAuthService;
    @Mock
    private SoptampUserService soptampUserService;
    @Mock
    private SoptampPointService soptampPointService;

    @InjectMocks
    private AuthFacade authFacade;

    @Test
    @DisplayName("SUCCESS_인증 회원 플그로 로그인")
    void SUCCESS_loginWithPlaygroundActive() {
        CodeRequest codeRequest = new CodeRequest();
        codeRequest.setCode("code");
        AccessTokenRequest accessTokenRequest = new AccessTokenRequest();
        accessTokenRequest.setAccessToken("accessToken");
        RefreshedToken refreshedToken = new RefreshedToken();
        refreshedToken.setAccessToken("refreshedToken");
        PlaygroundMain playgroundMain = new PlaygroundMain();
        playgroundMain.setName("name");
        playgroundMain.setStatus(UserStatus.ACTIVE);
        Id userId = Id.builder().id(21L).build();
        Long soptampUserId = 5L;
        AppToken appToken = AppToken.builder().accessToken("appAccessToken").refreshToken("appRefreshToken").build();
        Token token = new Token();
        token.setAccessToken("appAccessToken");
        token.setRefreshToken("appRefreshToken");

        when(playgroundAuthService.getPlaygroundAccessToken(codeRequest)).thenReturn(accessTokenRequest);
        when(playgroundAuthService.refreshPlaygroundToken(accessTokenRequest)).thenReturn(refreshedToken);
        when(playgroundAuthService.getPlaygroundInfo(refreshedToken.getAccessToken()))
                .thenReturn(playgroundMain);
        when(userService.loginWithUserPlaygroundId(playgroundMain)).thenReturn(userId);
        when(soptampUserService.updateSoptampUser(playgroundMain.getName(), userId.getId()))
                .thenReturn(soptampUserId);
        doNothing().when(soptampPointService).upsertSoptampPoint(playgroundMain.getStatus(), soptampUserId);
        when(jwtTokenService.issueNewTokens(userId, playgroundMain)).thenReturn(appToken);
        when(appAuthResponseMapper.of(appToken.getAccessToken(), appToken.getRefreshToken(),
                playgroundMain.getAccessToken(), playgroundMain.getStatus())).thenReturn(token);

        Token result = authFacade.loginWithPlayground(codeRequest);
        Assertions.assertEquals(appToken.getAccessToken(), result.getAccessToken());
        Assertions.assertEquals(appToken.getRefreshToken(), result.getRefreshToken());
    }

    @Test
    @DisplayName("SUCCESS_인증 회원 토큰 리프레시")
    void SUCCESS_getRefreshToken() {
        RefreshRequest refreshRequest = new RefreshRequest();
        refreshRequest.setRefreshToken("refreshToken");
        AccessTokenRequest accessTokenRequest = new AccessTokenRequest();
        accessTokenRequest.setAccessToken("accessToken");
        RefreshedToken refreshedToken = new RefreshedToken();
        refreshedToken.setAccessToken("refreshedToken");
        PlaygroundMain playgroundMain = new PlaygroundMain();
        playgroundMain.setAccessToken("accessToken");
        playgroundMain.setStatus(UserStatus.ACTIVE);
        Id userId = Id.builder().id(21L).build();
        AppToken newAppToken = AppToken.builder().accessToken("newAppAccessToken").refreshToken("newAppRefreshToken")
                .build();
        Token token = new Token();
        token.setAccessToken("newAppAccessToken");
        token.setRefreshToken("newAppRefreshToken");

        when(jwtTokenService.getUserIdFromJwtToken(refreshRequest.getRefreshToken())).thenReturn(userId);
        when(userService.getPlaygroundToken(userId)).thenReturn(accessTokenRequest);
        when(playgroundAuthService.refreshPlaygroundToken(accessTokenRequest)).thenReturn(refreshedToken);
        when(playgroundAuthService.getPlaygroundInfo(refreshedToken.getAccessToken()))
                .thenReturn(playgroundMain);
        doNothing().when(userService).updatePlaygroundToken(userId, refreshedToken.getAccessToken());
        when(jwtTokenService.issueNewTokens(userId, playgroundMain)).thenReturn(newAppToken);
        when(appAuthResponseMapper.of(newAppToken.getAccessToken(), newAppToken.getRefreshToken(),
                refreshedToken.getAccessToken(), playgroundMain.getStatus())).thenReturn(token);

        Token result = authFacade.getRefreshToken(refreshRequest);
        Assertions.assertEquals(newAppToken.getAccessToken(), result.getAccessToken());
        Assertions.assertEquals(newAppToken.getRefreshToken(), result.getRefreshToken());
    }
}
