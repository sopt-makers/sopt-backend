package org.sopt.app.application.auth;

import static org.sopt.app.domain.enums.FriendRecommendType.MBTI;
import static org.sopt.app.domain.enums.FriendRecommendType.UNIVERSITY;

import io.jsonwebtoken.ExpiredJwtException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.LongStream;
import lombok.RequiredArgsConstructor;
import lombok.val;
import org.sopt.app.application.auth.dto.PlaygroundAuthTokenInfo.RefreshedToken;
import org.sopt.app.application.auth.dto.PlaygroundPostInfo.PlaygroundPost;
import org.sopt.app.application.auth.dto.PlaygroundPostInfo.PlaygroundPostResponse;
import org.sopt.app.application.auth.dto.PlaygroundProfileInfo;
import org.sopt.app.application.auth.dto.PlaygroundProfileInfo.ActivityCardinalInfo;
import org.sopt.app.application.auth.dto.PlaygroundProfileInfo.MainView;
import org.sopt.app.application.auth.dto.PlaygroundProfileInfo.OwnPlaygroundProfile;
import org.sopt.app.application.auth.dto.PlaygroundProfileInfo.PlaygroundProfile;
import org.sopt.app.application.auth.dto.RecommendFriendRequest;
import org.sopt.app.application.auth.dto.RecommendedFriendInfo.RecommendFriendFilter;
import org.sopt.app.common.exception.BadRequestException;
import org.sopt.app.common.exception.UnauthorizedException;
import org.sopt.app.common.response.ErrorCode;
import org.sopt.app.domain.enums.UserStatus;
import org.sopt.app.interfaces.external.PlaygroundClient;
import org.sopt.app.presentation.auth.AppAuthRequest;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException.BadRequest;

@Service
@RequiredArgsConstructor
public class PlaygroundAuthService {

    private final PlaygroundUserRecommender playgroundUserRecommender;

    private final PlaygroundClient playgroundClient;

    @Value("${sopt.current.generation}")
    private Long currentGeneration;
    @Value("${makers.playground.x-api-key}")
    private String apiKey;
    @Value("${makers.playground.x-request-from}")
    private String requestFrom;
    @Value("${makers.playground.access-token}")
    private String playgroundToken;
    @Value("${makers.playground.web-page}")
    private String playgroundWebPageUrl;


    public PlaygroundProfileInfo.PlaygroundMain getPlaygroundInfo(String token) {
        val member = this.getPlaygroundMember(token);
        val playgroundProfile = this.getPlaygroundMemberProfile(token, member.getId());
        val generationList = this.getMemberGenerationList(playgroundProfile);
        member.setAccessToken(token);
        member.setStatus(this.getStatus(generationList));
        return member;
    }

    public AppAuthRequest.AccessTokenRequest getPlaygroundAccessToken(AppAuthRequest.CodeRequest codeRequest) {
        Map<String, String> headers = createDefaultHeader();
        try {
            return playgroundClient.getAccessToken(headers, codeRequest);
        } catch (Exception e) {
            throw new BadRequestException(ErrorCode.INVALID_PLAYGROUND_CODE.getMessage());
        }
    }

    private PlaygroundProfileInfo.PlaygroundMain getPlaygroundMember(String accessToken) {
        Map<String, String> headers = createAuthorizationHeader(accessToken);
        try {
            return playgroundClient.getPlaygroundMember(headers);
        } catch (ExpiredJwtException e) {
            throw new UnauthorizedException(ErrorCode.INVALID_PLAYGROUND_TOKEN.getMessage());
        } catch (Exception e) {
            throw new BadRequestException(ErrorCode.PLAYGROUND_USER_NOT_EXISTS.getMessage());
        }
    }

    public RefreshedToken refreshPlaygroundToken(AppAuthRequest.AccessTokenRequest tokenRequest) {
        Map<String, String> headers = createDefaultHeader();
        headers.put("x-api-key", apiKey);
        headers.put("x-request-from", requestFrom);
        try {
            return playgroundClient.refreshPlaygroundToken(headers, tokenRequest);
        } catch (BadRequest | ExpiredJwtException badRequest) {
            throw new UnauthorizedException(ErrorCode.INVALID_PLAYGROUND_TOKEN.getMessage());
        }
    }

    public PlaygroundProfileInfo.MainView getPlaygroundUserForMainView(String accessToken, Long playgroundId) {
        val playgroundProfile = this.getPlaygroundMemberProfile(accessToken, playgroundId);
        val profileImage = playgroundProfile.getProfileImage() == null ? "" : playgroundProfile.getProfileImage();
        val generationList = this.getMemberGenerationList(playgroundProfile);
        val mainViewUser = PlaygroundProfileInfo.MainViewUser.builder()
                .status(this.getStatus(generationList))
                .name(playgroundProfile.getName())
                .profileImage(profileImage)
                .generationList(generationList)
                .build();
        return new MainView(mainViewUser);
    }

    private UserStatus getStatus(List<Long> generationList) {
        return generationList.contains(currentGeneration) ? UserStatus.ACTIVE : UserStatus.INACTIVE;
    }

    private PlaygroundProfile getPlaygroundMemberProfile(String accessToken, Long playgroundId) {
        Map<String, String> headers = createAuthorizationHeader(accessToken);
        try {
            return playgroundClient.getSinglePlaygroundMemberProfile(headers, playgroundId).get(0);
        } catch (BadRequest e) {
            throw new BadRequestException(ErrorCode.PLAYGROUND_PROFILE_NOT_EXISTS.getMessage());
        } catch (ExpiredJwtException e) {
            throw new UnauthorizedException(ErrorCode.INVALID_PLAYGROUND_TOKEN.getMessage());
        }
    }

    public PlaygroundProfileInfo.UserActiveInfo getPlaygroundUserActiveInfo(String accessToken, Long playgroundId) {
        val playgroundProfile = this.getPlaygroundMemberProfile(accessToken, playgroundId);
        val generationList = this.getMemberGenerationList(playgroundProfile);
        val userStatus = this.getStatus(generationList);
        return new PlaygroundProfileInfo.UserActiveInfo(currentGeneration, userStatus);
    }

    private List<Long> getMemberGenerationList(PlaygroundProfileInfo.PlaygroundProfile playgroundProfile) {
        return playgroundProfile.getActivities().stream()
                .map(ActivityCardinalInfo::getGeneration)
                .sorted(Collections.reverseOrder())
                .toList();
    }

    // Header 생성 메서드
    private Map<String, String> createDefaultHeader() {
        return new HashMap<>(Map.of(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE));
    }

    private Map<String, String> createAuthorizationHeader(String accessToken) {
        Map<String, String> headers = createDefaultHeader();
        headers.put(HttpHeaders.AUTHORIZATION, accessToken);
        return headers;
    }

    public PlaygroundProfileInfo.ActiveUserIds getPlayGroundUserIds(String accessToken) {
        Map<String, String> requestHeader = createAuthorizationHeader(accessToken);
        try {
            return playgroundClient.getPlaygroundUserIds(requestHeader, currentGeneration);
        } catch (BadRequest e) {
            throw new BadRequestException(ErrorCode.PLAYGROUND_PROFILE_NOT_EXISTS.getMessage());
        } catch (ExpiredJwtException e) {
            throw new UnauthorizedException(ErrorCode.INVALID_PLAYGROUND_TOKEN.getMessage());
        }
    }

    public List<PlaygroundProfile> getPlaygroundMemberProfiles(String accessToken, List<Long> memberIds) {
        Map<String, String> requestHeader = createAuthorizationHeader(accessToken);
        String stringifyIds = memberIds.stream()
                .map(String::valueOf)
                .collect(Collectors.joining(","));
        try {
            return playgroundClient.getPlaygroundMemberProfiles(requestHeader,
                    URLEncoder.encode(stringifyIds, StandardCharsets.UTF_8));
        } catch (BadRequest e) {
            throw new BadRequestException(ErrorCode.PLAYGROUND_PROFILE_NOT_EXISTS.getMessage());
        } catch (ExpiredJwtException e) {
            throw new UnauthorizedException(ErrorCode.INVALID_PLAYGROUND_TOKEN.getMessage());
        }
    }

    public OwnPlaygroundProfile getOwnPlaygroundProfile(String accessToken) {
        Map<String, String> requestHeader = createAuthorizationHeader(accessToken);
        return playgroundClient.getOwnPlaygroundProfile(requestHeader);
    }

    public List<Long> getPlaygroundIdsForSameGeneration(List<Long> generationList) {

        return playgroundUserRecommender.getPlaygroundUserIdsForSameRecommendType(
                createAuthorizationHeader(playgroundToken),
                RecommendFriendRequest.createRecommendFriendRequestByGeneration(generationList)
        );
    }

    private List<Long> getGenerationListByLatestGenerationForRange(Long latestGeneration) {
        return LongStream.rangeClosed(0, 3)
                .mapToObj(i -> latestGeneration - i)
                .collect(Collectors.toList());
    }

    public List<Long> getPlaygroundIdsForSameMbti(Long latestGeneration, String mbti) {
        RecommendFriendRequest request =
                new RecommendFriendRequest(
                        getGenerationListByLatestGenerationForRange(latestGeneration),
                        List.of(RecommendFriendFilter.builder().key(String.valueOf(MBTI)).value(mbti).build()));
        return playgroundUserRecommender.getPlaygroundUserIdsForSameRecommendType(
                createAuthorizationHeader(playgroundToken),
                request
        );
    }

    public List<Long> getPlaygroundIdsForSameUniversity(Long latestGeneration, String university) {
        return playgroundUserRecommender.getPlaygroundUserIdsForSameRecommendType(
                createAuthorizationHeader(playgroundToken),
                new RecommendFriendRequest(
                        getGenerationListByLatestGenerationForRange(latestGeneration),
                        List.of(RecommendFriendFilter.builder().key(String.valueOf(UNIVERSITY)).value(university).build())
                )
        );
    }

    public PlaygroundPost getPlaygroundHotPost(String playgroundToken) {
        PlaygroundPostResponse postInfo = playgroundClient.getPlaygroundHotPost(createAuthorizationHeader(playgroundToken));

        return PlaygroundPost.builder()
                .title(postInfo.title())
                .content(postInfo.content())
                .url(convertPlaygroundWebPageUrl(postInfo.postId()))
                .build();
    }

    private String convertPlaygroundWebPageUrl(Long postId) {
        return playgroundWebPageUrl + "/?feed=" + postId;
    }
}
