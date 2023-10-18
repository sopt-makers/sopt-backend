package org.sopt.app.application.auth.client;

import feign.HeaderMap;
import feign.RequestLine;
import org.sopt.app.application.auth.PlaygroundAuthInfo;
import org.sopt.app.presentation.auth.AppAuthRequest;

import java.util.Map;

public interface PlaygroundClient {

    @RequestLine("POST /api/v1/idp/sso/auth")
    AppAuthRequest.AccessTokenRequest getAccessToken(@HeaderMap final Map<String, String> headers, final AppAuthRequest.CodeRequest codeRequest);

    @RequestLine("GET /internal/api/v1/members/me")
    PlaygroundAuthInfo.PlaygroundMain getPlaygroundMember(@HeaderMap Map<String, String> headers);

    @RequestLine("GET /internal/api/v1/members/profile/me")
    PlaygroundAuthInfo.PlaygroundProfile getPlaygroundMemberProfile(@HeaderMap Map<String, String> headers);

    @RequestLine("POST /internal/api/v1/idp/auth/token")
    PlaygroundAuthInfo.RefreshedToken refreshPlaygroundToken(@HeaderMap Map<String, String> headers, AppAuthRequest.AccessTokenRequest tokenRequest);
}
