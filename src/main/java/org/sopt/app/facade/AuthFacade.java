package org.sopt.app.facade;

import lombok.RequiredArgsConstructor;
import org.sopt.app.application.auth.JwtTokenService;
import org.sopt.app.application.auth.dto.PlaygroundAuthTokenInfo.*;
import org.sopt.app.application.playground.PlaygroundAuthService;
import org.sopt.app.application.playground.dto.PlaygroundProfileInfo.*;
import org.sopt.app.application.soptamp.SoptampUserService;
import org.sopt.app.application.user.UserService;
import org.sopt.app.presentation.auth.AppAuthRequest.*;
import org.sopt.app.presentation.auth.AppAuthResponse;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AuthFacade {

    private final JwtTokenService jwtTokenService;
    private final UserService userService;
    private final PlaygroundAuthService playgroundAuthService;
    private final SoptampUserService soptampUserService;

    @Transactional
    public AppAuthResponse loginWithPlayground(CodeRequest codeRequest) {
        String playgroundToken = this.getPlaygroundTokenByPlaygroundLogin(codeRequest);
        PlaygroundMain playgroundInfo = playgroundAuthService.getPlaygroundMember(playgroundToken);
        PlaygroundProfile playgroundProfile = playgroundAuthService.getPlaygroundMemberProfile(
                playgroundToken, playgroundInfo.getId()
        );

        Long userId = userService.upsertUser(LoginInfo.of(playgroundInfo, playgroundToken));
        soptampUserService.upsertSoptampUser(playgroundProfile, userId);

        AppToken appToken = jwtTokenService.issueNewTokens(userId, playgroundInfo.getId());
        return AppAuthResponse.builder()
                .playgroundToken(playgroundToken)
                .accessToken(appToken.getAccessToken())
                .refreshToken(appToken.getRefreshToken())
                .status(playgroundAuthService.getStatus(playgroundProfile.getLatestActivity().getGeneration()))
                .build();
    }

    private String getPlaygroundTokenByPlaygroundLogin(CodeRequest codeRequest){
        AccessTokenRequest temporaryToken = playgroundAuthService.getPlaygroundAccessToken(codeRequest);
        return playgroundAuthService.refreshPlaygroundToken(temporaryToken).getAccessToken();
    }

    @Transactional
    public AppAuthResponse getRefreshToken(String refreshToken) {
        Long userId = jwtTokenService.getUserIdFromJwtToken(refreshToken);
        AccessTokenRequest existingToken = userService.getPlaygroundToken(userId);
        String refreshedPlaygroundToken = playgroundAuthService.refreshPlaygroundToken(existingToken).getAccessToken();
        PlaygroundMain playgroundInfo = playgroundAuthService.getPlaygroundMember(refreshedPlaygroundToken);
        userService.updatePlaygroundToken(userId, refreshedPlaygroundToken);

        AppToken appToken = jwtTokenService.issueNewTokens(userId, playgroundInfo.getId());
        return AppAuthResponse.builder()
                .accessToken(appToken.getAccessToken())
                .playgroundToken(refreshedPlaygroundToken)
                .refreshToken(appToken.getRefreshToken())
                .status(playgroundAuthService.getStatus(playgroundInfo.getLatestGeneration()))
                .build();
    }

}
