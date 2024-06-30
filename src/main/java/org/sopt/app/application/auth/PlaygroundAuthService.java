package org.sopt.app.application.auth;

import static org.sopt.app.domain.enums.FriendRecommendType.MBTI;
import static org.sopt.app.domain.enums.FriendRecommendType.UNIVERSITY;

import io.jsonwebtoken.ExpiredJwtException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import lombok.RequiredArgsConstructor;
import lombok.val;
import org.sopt.app.application.auth.dto.PlaygroundProfileInfo;
import org.sopt.app.application.auth.dto.PlaygroundProfileInfo.OwnPlaygroundProfile;
import org.sopt.app.application.auth.dto.PlaygroundProfileInfo.PlaygroundProfile;
import org.sopt.app.application.auth.dto.PlaygroundProfileInfo.RecommendFriendFilter;
import org.sopt.app.application.auth.dto.PlaygroundProfileInfo.RecommendFriendRequest;
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

    private final PlaygroundClient playgroundClient;
    @Value("${sopt.current.generation}")
    private Long currentGeneration;
    @Value("${makers.playground.x-api-key}")
    private String apiKey;
    @Value("${makers.playground.x-request-from}")
    private String requestFrom;
    @Value("${makers.playground.access-token}")
    private String playgroundToken;

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

    public PlaygroundProfileInfo.RefreshedToken refreshPlaygroundToken(AppAuthRequest.AccessTokenRequest tokenRequest) {
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
        return PlaygroundProfileInfo.MainView.builder().user(mainViewUser).build();
    }

    private UserStatus getStatus(List<Long> generationList) {
        return generationList.contains(currentGeneration) ? UserStatus.ACTIVE : UserStatus.INACTIVE;
    }

    private PlaygroundProfileInfo.PlaygroundProfile getPlaygroundMemberProfile(String accessToken, Long playgroundId) {
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
        return PlaygroundProfileInfo.UserActiveInfo.builder()
                .status(userStatus)
                .currentGeneration(currentGeneration)
                .build();
    }

    private List<Long> getMemberGenerationList(PlaygroundProfileInfo.PlaygroundProfile playgroundProfile) {
        return playgroundProfile.getActivities().stream()
                .map(activity ->
                {
                    try {
                        return Long.parseLong(activity.getCardinalInfo().split(",")[0]);
                    } catch (NumberFormatException e) {
                        throw new BadRequestException(ErrorCode.INVALID_PLAYGROUND_CARDINAL_INFO.getMessage());
                    }
                })
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

    public List<PlaygroundProfile> getPlaygroundMemberProfiles(String accessToken,
            List<Long> memberIds) {
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

    public List<Long> getPlaygroundIdsForSameGeneration(List<Integer> generationList) {

        return playgroundClient.getPlaygroundUserIdsForSameRecommendType(
                createAuthorizationHeader(playgroundToken),
                RecommendFriendRequest.createRecommendFriendRequestByGeneration(generationList)
        ).getUserIds();
    }

    private List<Integer> getGenerationListByLatestGenerationForRange(Integer latestGeneration) {
        return IntStream.rangeClosed(0, 3)
                .mapToObj(i -> latestGeneration - i)
                .collect(Collectors.toList());
    }

    public List<Long> getPlaygroundIdsForSameMbti(Integer latestGeneration, String mbti) {
        RecommendFriendRequest request = RecommendFriendRequest.builder()
                .generations(getGenerationListByLatestGenerationForRange(latestGeneration))
                .filters(List.of(RecommendFriendFilter.builder().key(String.valueOf(MBTI)).value(mbti).build()))
                .build();
        return playgroundClient.getPlaygroundUserIdsForSameRecommendType(
                createAuthorizationHeader(playgroundToken),
                request
        ).getUserIds();
    }

    public List<Long> getPlaygroundIdsForSameUniversity(Integer latestGeneration, String university) {
        RecommendFriendRequest request = RecommendFriendRequest.builder()
                .generations(getGenerationListByLatestGenerationForRange(latestGeneration))
                .filters(List.of(RecommendFriendFilter.builder().key(String.valueOf(UNIVERSITY)).value(university).build()))
                .build();
        return playgroundClient.getPlaygroundUserIdsForSameRecommendType(
                createAuthorizationHeader(playgroundToken),
                request
        ).getUserIds();
    }
}
