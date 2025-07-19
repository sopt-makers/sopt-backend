package org.sopt.app.application.playground;

import java.util.List;

import org.sopt.app.application.playground.dto.PlayGroundUserSoptLevelResponse;
import org.sopt.app.application.playground.dto.PlaygroundPopularPost;
import org.sopt.app.application.playground.dto.PlaygroundProfileInfo.OwnPlaygroundProfile;
import org.sopt.app.application.playground.dto.PlaygroundProfileInfo.PlaygroundProfile;
import org.sopt.app.application.playground.dto.PlaygroundRecentPost;
import org.sopt.app.application.playground.dto.PlaygroundUserFindCondition;
import org.sopt.app.application.playground.dto.RecommendedFriendInfo.PlaygroundUserIds;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.web.bind.annotation.RequestBody;

import feign.Param;
import feign.RequestLine;

@EnableFeignClients
public interface PlaygroundClient {

    // headers 제외
    @RequestLine("GET /internal/api/v1/members/profile?memberIds={memberId}")
    List<PlaygroundProfile> getPlaygroundMemberProfiles(@Param("memberId") Long playgroundId);

    // headers 제외
    @RequestLine("GET /internal/api/v1/members/profile?memberIds={encodedIds}")
    List<PlaygroundProfile> getPlaygroundMemberProfiles(@Param(value = "encodedIds") String encodedIds);

    // headers 제외, userId
    @RequestLine("GET /api/v1/members/profile/me?memberId={memberId}")
    OwnPlaygroundProfile getOwnPlaygroundProfile(@Param("memberId") Long userId);

    // header 제외
    @RequestLine("POST /internal/api/v1/members/profile/recommend")
    PlaygroundUserIds getPlaygroundUserIdsByCondition(@RequestBody PlaygroundUserFindCondition condition);

    // headermap 제외
    @RequestLine("GET /internal/api/v1/members/{memberId}/project")
    PlayGroundUserSoptLevelResponse getPlayGroundUserSoptLevel(@Param Long memberId);

    // headermap 제외 memberId 추가
    @RequestLine("GET /api/v1/members/profile/me?memberId={memberId}")
    PlaygroundProfile getPlayGroundProfile(@Param Long memberId);

    // headermap 제외
    @RequestLine("GET /internal/api/v1/community/posts/latest")
    List<PlaygroundRecentPost> getPlaygroundRecentPosts();

    // headermap 제외
    @RequestLine("GET /internal/api/v1/community/posts/popular")
    List<PlaygroundPopularPost> getPlaygroundPopularPosts();
}
