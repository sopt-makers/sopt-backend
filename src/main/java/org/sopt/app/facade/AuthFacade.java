package org.sopt.app.facade;

import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.sopt.app.application.auth.JwtTokenService;
import org.sopt.app.application.auth.dto.PlaygroundAuthTokenInfo.AppToken;
import org.sopt.app.application.playground.PlaygroundAuthService;
import org.sopt.app.application.playground.dto.PlaygroundProfileInfo.LoginInfo;
import org.sopt.app.application.playground.dto.PlaygroundProfileInfo.PlaygroundMain;
import org.sopt.app.application.playground.dto.PlaygroundProfileInfo.PlaygroundProfile;
import org.sopt.app.application.poke.PokeService;
import org.sopt.app.application.soptamp.SoptampUserService;
import org.sopt.app.application.user.UserService;
import org.sopt.app.domain.entity.User;
import org.sopt.app.domain.enums.IconType;
import org.sopt.app.presentation.auth.AppAuthRequest.AccessTokenRequest;
import org.sopt.app.presentation.auth.AppAuthRequest.CodeRequest;
import org.sopt.app.presentation.auth.AppAuthResponse;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthFacade {

    private final JwtTokenService jwtTokenService;
    private final UserService userService;
    private final PlaygroundAuthService playgroundAuthService;
    private final SoptampUserService soptampUserService;
    private final PokeService pokeService;

    @Transactional
    public AppAuthResponse loginWithPlayground(CodeRequest codeRequest) {
        String playgroundToken = this.getPlaygroundTokenByPlaygroundLogin(codeRequest);
        PlaygroundMain playgroundInfo = playgroundAuthService.getPlaygroundMember(playgroundToken);
        PlaygroundProfile playgroundProfile = playgroundAuthService.getPlaygroundMemberProfile(
                playgroundToken, playgroundInfo.getId()
        );
        Long latestGeneration = playgroundProfile.getLatestActivity().getGeneration();
        log.error("latestGeneration: {}", latestGeneration);
        log.error("playgroundProfile.getGeneration: {}", playgroundProfile.getLatestActivity().getGeneration());
        Long userId = userService.upsertUser(LoginInfo.of(playgroundInfo, playgroundToken));
        soptampUserService.upsertSoptampUser(playgroundProfile, userId);

//        if (playgroundAuthService.isCurrentGeneration(latestGeneration)){
//            soptampUserService.upsertSoptampUser(playgroundProfile, userId);
//        }

        AppToken appToken = jwtTokenService.issueNewTokens(userId, playgroundInfo.getId());
        return AppAuthResponse.builder()
                .playgroundToken(playgroundToken)
                .accessToken(appToken.getAccessToken())
                .refreshToken(appToken.getRefreshToken())
                .status(playgroundAuthService.getStatus(latestGeneration))
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

    public int getUserSoptLevel(User user) {
        return playgroundAuthService.getUserSoptLevel(user);
    }

    public PlaygroundProfile getUserDetails(User user) {
        return playgroundAuthService.getPlayGroundProfile(user.getPlaygroundToken());
    }

    public Long getDuration(Long myGeneration, Long generation) {
        return userService.getDuration(myGeneration, generation);
    }

    public List<String> getIcons(IconType iconType) {
        return userService.getIcons(iconType);
    }
}
