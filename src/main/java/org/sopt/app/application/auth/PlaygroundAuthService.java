package org.sopt.app.application.auth;

import lombok.RequiredArgsConstructor;
import lombok.val;
import org.sopt.app.presentation.auth.AuthRequest;
import org.sopt.app.presentation.auth.AuthResponse;
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

    private RestTemplate restTemplate = new RestTemplate();

    public AuthResponse.PlaygroundResponse getPlaygroundInfo(AuthRequest.CodeRequest codeRequest) {
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

    private AuthResponse.PlaygroundResponse getPlaygroundMember(String accessToken) {
        val getUserURL = baseURI + "/internal/api/v1/members/me";

        val headers = new HttpHeaders();
        headers.add("content-type", "application/json;charset=UTF-8");
        headers.add("Authorization", accessToken);

        val entity = new HttpEntity(null, headers);

        val response = restTemplate.exchange(
                getUserURL,
                HttpMethod.GET,
                entity,
                AuthResponse.PlaygroundResponse.class
        );
        return response.getBody();
    }


}
