package org.sopt.app.facade;

import lombok.RequiredArgsConstructor;
import lombok.val;
import org.sopt.app.application.auth.JwtTokenService;
import org.sopt.app.application.playground.PlaygroundAuthService;
import org.sopt.app.application.soptamp.SoptampUserService;
import org.sopt.app.application.user.UserService;
import org.sopt.app.presentation.auth.AppAuthRequest;
import org.sopt.app.presentation.auth.AppAuthRequest.RefreshRequest;
import org.sopt.app.presentation.auth.AppAuthResponse.Token;
import org.sopt.app.presentation.auth.AppAuthResponseMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AuthFacade {

    private final JwtTokenService jwtTokenService;
    private final UserService userService;
    private final PlaygroundAuthService playgroundAuthService;
    private final AppAuthResponseMapper authResponseMapper;
    private final SoptampUserService soptampUserService;

    @Transactional
    public Token loginWithPlayground(AppAuthRequest.CodeRequest codeRequest) {
        // PlayGround SSO Auth 를 통해 accessToken 받아옴
        val temporaryToken = playgroundAuthService.getPlaygroundAccessToken(codeRequest);
        // PlayGround Auth Access Token 받옴
        val playgroundToken = playgroundAuthService.refreshPlaygroundToken(temporaryToken);
        // PlayGround User Info 받아옴
        val playgroundMember = playgroundAuthService.getPlaygroundInfo(playgroundToken.getAccessToken());
        val userId = userService.loginWithUserPlaygroundId(playgroundMember);
        soptampUserService.createSoptampUser(playgroundMember.getName(), userId.getId());

        // Response 할 Body 생성
        val appToken = jwtTokenService.issueNewTokens(userId, playgroundMember);
        return authResponseMapper.of(
            appToken.getAccessToken()
            , appToken.getRefreshToken()
            , playgroundMember.getAccessToken()
            , playgroundMember.getStatus()
        );
    }

    @Transactional
    public Token getRefreshToken(RefreshRequest refreshRequest) {
        val userId = jwtTokenService.getUserIdFromJwtToken(refreshRequest.getRefreshToken());
        val existingToken = userService.getPlaygroundToken(userId);
        val playgroundToken = playgroundAuthService.refreshPlaygroundToken(existingToken);
        val playgroundMember = playgroundAuthService.getPlaygroundInfo(playgroundToken.getAccessToken());
        userService.updatePlaygroundToken(userId, playgroundToken.getAccessToken());

        val appToken = jwtTokenService.issueNewTokens(userId, playgroundMember);
        return authResponseMapper.of(appToken.getAccessToken(), appToken.getRefreshToken(),
            playgroundToken.getAccessToken(), playgroundMember.getStatus());
    }

}
