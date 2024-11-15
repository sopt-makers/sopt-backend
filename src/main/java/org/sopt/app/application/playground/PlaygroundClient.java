package org.sopt.app.application.playground;


import feign.HeaderMap;
import feign.Param;
import feign.RequestLine;
import java.util.List;
import java.util.Map;
import org.sopt.app.application.auth.dto.PlaygroundAuthTokenInfo.RefreshedToken;
import org.sopt.app.application.playground.dto.PlayGroundEmploymentResponse;
import org.sopt.app.application.playground.dto.PlaygroundPostInfo.PlaygroundPostResponse;
import org.sopt.app.application.playground.dto.PlaygroundProfileInfo.ActiveUserIds;
import org.sopt.app.application.playground.dto.PlaygroundProfileInfo.OwnPlaygroundProfile;
import org.sopt.app.application.playground.dto.PlaygroundProfileInfo.PlaygroundMain;
import org.sopt.app.application.playground.dto.PlaygroundProfileInfo.PlaygroundProfile;
import org.sopt.app.application.playground.dto.PlaygroundUserFindCondition;
import org.sopt.app.application.playground.dto.RecommendedFriendInfo.PlaygroundUserIds;
import org.sopt.app.presentation.auth.AppAuthRequest.*;
import org.sopt.app.presentation.home.response.RecentPostsResponse;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.web.bind.annotation.RequestBody;

@EnableFeignClients
public interface PlaygroundClient {

    @RequestLine("POST /api/v1/idp/sso/auth")
    AccessTokenRequest getAccessToken(@HeaderMap final Map<String, String> headers, final CodeRequest codeRequest);

    @RequestLine("POST /internal/api/v1/idp/auth/token")
    RefreshedToken refreshPlaygroundToken(@HeaderMap Map<String, String> headers, AccessTokenRequest tokenRequest);

    @RequestLine("GET /internal/api/v1/members/latest?generation={generation}")
    ActiveUserIds getPlaygroundUserIds(@HeaderMap Map<String, String> headers, @Param("generation") Long generation);

    @RequestLine("GET /internal/api/v1/members/profile?memberIds={memberId}")
    List<PlaygroundProfile> getPlaygroundMemberProfiles(@HeaderMap Map<String, String> headers,
                                                        @Param("memberId") Long playgroundId);

    @RequestLine("GET /internal/api/v1/members/profile?memberIds={encodedIds}")
    List<PlaygroundProfile> getPlaygroundMemberProfiles(@HeaderMap Map<String, String> headers,
                                                        @Param(value = "encodedIds") String encodedIds);

    @RequestLine("GET /internal/api/v1/members/me")
    PlaygroundMain getPlaygroundMember(@HeaderMap Map<String, String> headers);

    @RequestLine("GET /api/v1/members/profile/me")
    OwnPlaygroundProfile getOwnPlaygroundProfile(@HeaderMap Map<String, String> headers);

    @RequestLine("POST /internal/api/v1/members/profile/recommend")
    PlaygroundUserIds getPlaygroundUserIdsByCondition(@HeaderMap Map<String, String> headers,
                                                      @RequestBody PlaygroundUserFindCondition condition);

    @RequestLine("GET /api/v1/community/posts/hot")
    PlaygroundPostResponse getPlaygroundHotPost(@HeaderMap Map<String, String> headers);

    @RequestLine("GET /internal/api/v1/community/post/recent?category={category}")
    RecentPostsResponse getRecentPosts(@HeaderMap Map<String, String> headers, @Param("category") String category);

    @RequestLine("GET /api/v1/community/posts?categoryId={categoryId}&limit={limit}&cursor={cursor}")
    PlayGroundEmploymentResponse getPlaygroundEmploymentPost(@HeaderMap Map<String, String> headers,
                                                             @Param int categoryId,
                                                             @Param int limit,
                                                             @Param int cursor);
}
