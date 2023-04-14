package org.sopt.app.application.auth;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.val;
import org.sopt.app.common.exception.BadRequestException;
import org.sopt.app.common.exception.UnauthorizedException;
import org.sopt.app.common.response.ErrorCode;
import org.sopt.app.domain.enums.UserStatus;
import org.sopt.app.presentation.auth.AuthRequest;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException.BadRequest;
import org.springframework.web.client.RestTemplate;

@Service
@RequiredArgsConstructor
public class PlaygroundAuthService {

    private final RestTemplate restTemplate;
    @Value("${makers.playground.server}")
    private String baseURI;
    @Value("${sopt.current.generation}")
    private Long currentGeneration;
    @Value("${makers.playground.x-api-key}")
    private String apiKey;
    @Value("${makers.playground.x-request-from}")
    private String requestFrom;

    public PlaygroundAuthInfo.PlaygroundMain getPlaygroundInfo(String token) {
        val member = this.getPlaygroundMember(token);
        val playgroundProfile = this.getPlaygroundMemberProfile(token);
        val generationList = playgroundProfile.getActivities().stream()
                .map(activity -> activity.getCardinalActivities().get(0).getGeneration()).collect(Collectors.toList());
        member.setAccessToken(token);
        member.setStatus(this.getStatus(generationList));
        return member;
    }

    public AuthRequest.AccessTokenRequest getPlaygroundAccessToken(AuthRequest.CodeRequest codeRequest) {
        val getTokenURL = baseURI + "/api/v1/idp/sso/auth";

        val headers = new HttpHeaders();
        headers.add("content-type", "application/json;charset=UTF-8");

        val entity = new HttpEntity(codeRequest, headers);

        val response = restTemplate.exchange(
                getTokenURL,
                HttpMethod.POST,
                entity,
                AuthRequest.AccessTokenRequest.class
        );
        return response.getBody();
    }

    private PlaygroundAuthInfo.PlaygroundMain getPlaygroundMember(String accessToken) {
        val getUserURL = baseURI + "/internal/api/v1/members/me";

        val headers = new HttpHeaders();
        headers.add("content-type", "application/json;charset=UTF-8");
        headers.add("Authorization", accessToken);

        val entity = new HttpEntity(null, headers);

        val response = restTemplate.exchange(
                getUserURL,
                HttpMethod.GET,
                entity,
                PlaygroundAuthInfo.PlaygroundMain.class
        );
        return response.getBody();
    }

    public PlaygroundAuthInfo.RefreshedToken refreshPlaygroundToken(AuthRequest.AccessTokenRequest tokenRequest) {
        val getTokenURL = baseURI + "/internal/api/v1/idp/auth/token";

        val headers = new HttpHeaders();
        headers.add("content-type", "application/json;charset=UTF-8");
        headers.add("x-api-key", apiKey);
        headers.add("x-request-from", requestFrom);

        val entity = new HttpEntity(tokenRequest, headers);

        try {
            val response = restTemplate.exchange(
                    getTokenURL,
                    HttpMethod.POST,
                    entity,
                    PlaygroundAuthInfo.RefreshedToken.class
            );
            return response.getBody();
        } catch (BadRequest badRequest) {
            throw new UnauthorizedException(ErrorCode.INVALID_PLAYGROUND_TOKEN.getMessage());
        }
    }

    public PlaygroundAuthInfo.MainView getPlaygroundUserForMainView(String accessToken) {
        val playgroundProfile = this.getPlaygroundMemberProfile(accessToken);
        val generationList = playgroundProfile.getActivities().stream()
                .map(activity -> activity.getCardinalActivities().get(0).getGeneration()).collect(Collectors.toList());
        Collections.sort(generationList, Collections.reverseOrder());
        val mainViewUser = PlaygroundAuthInfo.MainViewUser.builder()
                .status(this.getStatus(generationList))
                .name(playgroundProfile.getName())
                .profileImage(playgroundProfile.getProfileImage())
                .generationList(generationList)
                .build();
        return PlaygroundAuthInfo.MainView.builder().user(mainViewUser).build();
    }

    private UserStatus getStatus(List<Long> generationList) {
        return generationList.contains(currentGeneration) ? UserStatus.ACTIVE : UserStatus.INACTIVE;
    }

    private PlaygroundAuthInfo.PlaygroundProfile getPlaygroundMemberProfile(String accessToken) {
        val getUserURL = baseURI + "/internal/api/v1/members/profile/me";

        val headers = new HttpHeaders();
        headers.add("content-type", "application/json;charset=UTF-8");
        headers.add("Authorization", accessToken);

        val entity = new HttpEntity(null, headers);

        try {
            val response = restTemplate.exchange(
                    getUserURL,
                    HttpMethod.GET,
                    entity,
                    PlaygroundAuthInfo.PlaygroundProfile.class
            );
            return response.getBody();
        } catch (BadRequest e) {
            throw new BadRequestException(ErrorCode.PLAYGROUND_PROFILE_NOT_EXISTS.getMessage());
        }
    }

}
