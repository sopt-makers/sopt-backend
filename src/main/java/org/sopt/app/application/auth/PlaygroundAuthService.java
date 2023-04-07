package org.sopt.app.application.auth;

import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.val;
import org.sopt.app.domain.enums.UserStatus;
import org.sopt.app.presentation.auth.AuthRequest;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
@RequiredArgsConstructor
public class PlaygroundAuthService {

    @Value("${makers.playground.server.dev}")
    private String baseURI;

    @Value("${sopt.current.generation}")
    private Long currentGeneration;

    private RestTemplate restTemplate = new RestTemplate();

    public PlaygroundAuthInfo.PlaygroundMain getPlaygroundInfo(AuthRequest.CodeRequest codeRequest) {
        val tokenRequest = this.getPlaygroundAccessToken(codeRequest);
        val member = this.getPlaygroundMember(tokenRequest.getAccessToken());
        member.setAccessToken(tokenRequest.getAccessToken());
        return member;
    }

    private AuthRequest.AccessTokenRequest getPlaygroundAccessToken(AuthRequest.CodeRequest codeRequest) {
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

    public PlaygroundAuthInfo.MainView getPlaygroundUserForMainView(String accessToken) {
        val playgroundProfile = getPlaygroundMemberProfile(accessToken);
        val generationList = playgroundProfile.getActivities().stream()
                .map(activity -> activity.getCardinalActivities().get(0).getGeneration()).collect(Collectors.toList());
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

        val response = restTemplate.exchange(
                getUserURL,
                HttpMethod.GET,
                entity,
                PlaygroundAuthInfo.PlaygroundProfile.class
        );
        return response.getBody();
    }

}
