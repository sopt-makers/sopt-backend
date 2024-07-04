package org.sopt.app.interfaces.external;

import feign.HeaderMap;
import feign.Param;
import feign.RequestLine;
import org.sopt.app.application.auth.PlaygroundAuthInfo;
import org.sopt.app.application.auth.PlaygroundAuthInfo.ActiveUserIds;
import org.sopt.app.application.auth.PlaygroundAuthInfo.PlaygroundProfile;
import org.sopt.app.application.auth.PlaygroundAuthInfo.PlaygroundUserIds;
import org.sopt.app.application.auth.RecommendFriendRequest;
import org.sopt.app.presentation.auth.AppAuthRequest;

import java.util.List;
import java.util.Map;
import org.springframework.web.bind.annotation.RequestBody;

public interface PlaygroundClient {

    @RequestLine("POST /api/v1/idp/sso/auth")
    AppAuthRequest.AccessTokenRequest getAccessToken(@HeaderMap final Map<String, String> headers,
            final AppAuthRequest.CodeRequest codeRequest);

    @RequestLine("POST /internal/api/v1/idp/auth/token")
    PlaygroundAuthInfo.RefreshedToken refreshPlaygroundToken(@HeaderMap Map<String, String> headers,
            AppAuthRequest.AccessTokenRequest tokenRequest);

    @RequestLine("GET /internal/api/v1/members/latest?generation={generation}")
    ActiveUserIds getPlaygroundUserIds(@HeaderMap Map<String, String> headers, @Param("generation") Integer generation);

    @RequestLine("GET /internal/api/v1/members/profile?memberIds={memberId}")
    List<PlaygroundProfile> getSinglePlaygroundMemberProfile(@HeaderMap Map<String, String> headers,
            @Param("memberId") Long playgroundId);

    @RequestLine("GET /internal/api/v1/members/profile?memberIds={encodedIds}")
    List<PlaygroundProfile> getPlaygroundMemberProfiles(@HeaderMap Map<String, String> headers,
            @Param(value = "encodedIds") String encodedIds);

    @RequestLine("GET /internal/api/v1/members/me")
    PlaygroundAuthInfo.PlaygroundMain getPlaygroundMember(@HeaderMap Map<String, String> headers);

    @RequestLine("GET /api/v1/members/profile/me")
    PlaygroundAuthInfo.OwnPlaygroundProfile getOwnPlaygroundProfile(@HeaderMap Map<String, String> headers);

    @RequestLine("POST /internal/api/v1/members/profile/recommend")
    PlaygroundUserIds getPlaygroundUserIdsForSameRecommendType(
            @HeaderMap Map<String, String> headers, @RequestBody RecommendFriendRequest request);
}
