package org.sopt.app.application.auth;

import lombok.RequiredArgsConstructor;
import org.sopt.app.presentation.auth.AuthRequest;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
@RequiredArgsConstructor
public class AuthService {

    @Value("${makers.playground.server.dev}")
    private String baseURI;

    public AuthRequest.AccessTokenRequest getPlaygroundAccessToken(AuthRequest.CodeRequest codeRequest) {
        String getTokenURL = baseURI + "/api/v1/idp/sso/auth";

        HttpHeaders headers = new HttpHeaders();
        headers.add("content-type", "application/json;charset=UTF-8");

        HttpEntity<AuthRequest.CodeRequest> entity = new HttpEntity(codeRequest, headers);

        RestTemplate rt = new RestTemplate();
        ResponseEntity<AuthRequest.AccessTokenRequest> response = rt.exchange(
                getTokenURL,
                HttpMethod.POST,
                entity,
                AuthRequest.AccessTokenRequest.class
        );
        return response.getBody();
    }

    public Object getPlaygroundUser(String accessToken) {
        String getUserURL = baseURI + "/api/v1/members/me";

        HttpHeaders headers = new HttpHeaders();
        headers.add("content-type", "application/json;charset=UTF-8");
        headers.add("Authorization", accessToken);

        HttpEntity<AuthRequest.CodeRequest> entity = new HttpEntity(null, headers);

        RestTemplate rt = new RestTemplate();
        ResponseEntity<Object> response = rt.exchange(
                getUserURL,
                HttpMethod.GET,
                entity,
                Object.class
        );
        return response.getBody();
    }
}
