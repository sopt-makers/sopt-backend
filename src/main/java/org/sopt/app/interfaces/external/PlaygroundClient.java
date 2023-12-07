package org.sopt.app.interfaces.external;

import feign.HeaderMap;
import feign.Param;
import feign.RequestLine;
import org.sopt.app.application.auth.PlaygroundAuthInfo;
import org.sopt.app.application.auth.PlaygroundAuthInfo.ActiveUserIds;
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

    @RequestLine("GET /internal/api/v1/members/latest?generation={generation}")
    ActiveUserIds getPlaygroundUserIds(@HeaderMap Map<String, String> headers, @Param("generation") Long generation);

}
