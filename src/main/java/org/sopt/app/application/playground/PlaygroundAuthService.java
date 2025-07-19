package org.sopt.app.application.playground;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.stream.Collectors;

import org.sopt.app.application.playground.dto.PlaygroundPopularPost;
import org.sopt.app.application.playground.dto.PlaygroundProfileInfo;
import org.sopt.app.application.playground.dto.PlaygroundProfileInfo.OwnPlaygroundProfile;
import org.sopt.app.application.playground.dto.PlaygroundProfileInfo.PlaygroundProfile;
import org.sopt.app.application.playground.dto.PlaygroundRecentPost;
import org.sopt.app.common.exception.BadRequestException;
import org.sopt.app.common.response.ErrorCode;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException.BadRequest;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class PlaygroundAuthService {

    private final PlaygroundClient playgroundClient;

    @Value("${sopt.current.generation}")
    private Long currentGeneration;
    @Value("${makers.playground.x-api-key}")
    private String apiKey;
    @Value("${makers.playground.x-request-from}")
    private String requestFrom;
    @Value("${makers.playground.web-page}")
    private String playgroundWebPageUrl;

    public PlaygroundProfile getPlaygroundMemberProfile(Long userId) {
        try {
            return playgroundClient.getPlaygroundMemberProfiles(userId).getFirst();
        } catch (BadRequest e) {
            throw new BadRequestException(ErrorCode.PLAYGROUND_PROFILE_NOT_EXISTS);
        }
    }

    public List<PlaygroundProfile> getPlaygroundMemberProfiles(List<Long> userIds) {
        String stringifyIds = userIds.stream()
                .map(String::valueOf)
                .collect(Collectors.joining(","));
        try {
            return playgroundClient.getPlaygroundMemberProfiles(URLEncoder.encode(stringifyIds, StandardCharsets.UTF_8));
        } catch (BadRequest e) {
            throw new BadRequestException(ErrorCode.PLAYGROUND_PROFILE_NOT_EXISTS);
        }
    }

    public OwnPlaygroundProfile getOwnPlaygroundProfile(Long userId) {
        return playgroundClient.getOwnPlaygroundProfile(userId);
    }

    private String convertPlaygroundWebPageUrl(Long postId) {
        return playgroundWebPageUrl + "/?feed=" + postId;
    }

    public int getUserSoptLevel(Long userId) {
        return playgroundClient.getPlayGroundUserSoptLevel(userId).soptProjectCount();
    }

    public PlaygroundProfile getPlayGroundProfile(Long userId) {
        return playgroundClient.getPlayGroundProfile(userId);
    }
    public List<PlaygroundRecentPost> getPlaygroundRecentPosts() {
        return playgroundClient.getPlaygroundRecentPosts();
    }

    public List<PlaygroundPopularPost> getPlaygroundPopularPosts() {
        return playgroundClient.getPlaygroundPopularPosts();
    }
}
