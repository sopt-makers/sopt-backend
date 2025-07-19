package org.sopt.app.application.playground;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.sopt.app.application.auth.dto.PlaygroundAuthTokenInfo.RefreshedToken;
import org.sopt.app.application.playground.dto.PlayGroundCoffeeChatResponse;
import org.sopt.app.application.playground.dto.PlayGroundEmploymentResponse;
import org.sopt.app.application.playground.dto.PlayGroundPostCategory;
import org.sopt.app.application.playground.dto.PlayGroundPostDetailResponse;
import org.sopt.app.application.playground.dto.PlaygroundPopularPost;
import org.sopt.app.application.playground.dto.PlaygroundPostInfo.PlaygroundPost;
import org.sopt.app.application.playground.dto.PlaygroundPostInfo.PlaygroundPostResponse;
import org.sopt.app.application.playground.dto.PlaygroundProfileInfo.ActiveUserIds;
import org.sopt.app.application.playground.dto.PlaygroundProfileInfo.ActivityCardinalInfo;
import org.sopt.app.application.playground.dto.PlaygroundProfileInfo.MainView;
import org.sopt.app.application.playground.dto.PlaygroundProfileInfo.MainViewUser;

import org.sopt.app.application.playground.dto.PlaygroundProfileInfo.OwnPlaygroundProfile;
import org.sopt.app.application.playground.dto.PlaygroundProfileInfo.PlaygroundProfile;
import org.sopt.app.application.playground.dto.PlaygroundProfileInfo.UserActiveInfo;
import org.sopt.app.application.playground.dto.PlaygroundRecentPost;
import org.sopt.app.application.playground.dto.PostWithMemberInfo;
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

    // public AccessTokenRequest getPlaygroundAccessToken(CodeRequest codeRequest) {
    //     try {
    //         return playgroundClient.getAccessToken(createDefaultHeader(), codeRequest);
    //     } catch (Exception e) {
    //         throw new BadRequestException(ErrorCode.INVALID_PLAYGROUND_CODE);
    //     }
    // }

    // public PlaygroundMain getPlaygroundMember(String accessToken) {
    //     try {
    //         return playgroundClient.getPlaygroundMember(createAuthorizationHeaderByUserPlaygroundToken(accessToken));
    //     } catch (ExpiredJwtException e) {
    //         throw new UnauthorizedException(ErrorCode.INVALID_PLAYGROUND_TOKEN);
    //     } catch (Exception e) {
    //         throw new BadRequestException(ErrorCode.PLAYGROUND_USER_NOT_EXISTS);
    //     }
    // }

    // public RefreshedToken refreshPlaygroundToken(AccessTokenRequest tokenRequest) {
    //     Map<String, String> headers = createDefaultHeader();
    //     headers.put("x-api-key", apiKey);
    //     headers.put("x-request-from", requestFrom);
    //     try {
    //         return playgroundClient.refreshPlaygroundToken(headers, tokenRequest);
    //     } catch (BadRequest | ExpiredJwtException badRequest) {
    //         throw new UnauthorizedException(ErrorCode.INVALID_PLAYGROUND_TOKEN);
    //     }
    // }

    // TODO : 사용여부 확인 필요
    // public MainView getPlaygroundUserForMainView(String accessToken, Long playgroundId) {
    //     val playgroundProfile = this.getPlaygroundMemberProfile(accessToken, playgroundId);
    //     val profileImage = playgroundProfile.getProfileImage() == null ? "" : playgroundProfile.getProfileImage();
    //     val generationList = this.getMemberGenerationList(playgroundProfile);
    //     val mainViewUser = MainViewUser.builder()
    //             .status(this.getStatus(generationList))
    //             .name(playgroundProfile.getName())
    //             .profileImage(profileImage)
    //             .generationList(generationList)
    //             .build();
    //     return new MainView(mainViewUser);
    // }

    // private UserStatus getStatus(List<Long> generationList) {
    //     return generationList.contains(currentGeneration) ? UserStatus.ACTIVE : UserStatus.INACTIVE;
    // }
    //
    // public UserStatus getStatus(Long latestGeneration) {
    //     return latestGeneration.equals(currentGeneration) ? UserStatus.ACTIVE : UserStatus.INACTIVE;
    // }

    public PlaygroundProfile getPlaygroundMemberProfile(Long userId) {
        try {
            return playgroundClient.getPlaygroundMemberProfiles(userId).getFirst();
        } catch (BadRequest e) {
            throw new BadRequestException(ErrorCode.PLAYGROUND_PROFILE_NOT_EXISTS);
        }
    }

    // public UserActiveInfo getPlaygroundUserActiveInfo(String accessToken, Long playgroundId) {
    //     val playgroundProfile = this.getPlaygroundMemberProfile(accessToken, playgroundId);
    //     val generationList = this.getMemberGenerationList(playgroundProfile);
    //     val userStatus = this.getStatus(generationList);
    //     return new UserActiveInfo(currentGeneration, userStatus);
    // }

    // private List<Long> getMemberGenerationList(PlaygroundProfile playgroundProfile) {
    //     return playgroundProfile.getActivities().stream()
    //             .map(ActivityCardinalInfo::getGeneration)
    //             .sorted(Collections.reverseOrder())
    //             .toList();
    // }

    // TODO: 플폼에 new internal api 요청
    // public ActiveUserIds getPlayGroundUserIds(String accessToken) {
    //     Map<String, String> requestHeader = createAuthorizationHeaderByUserPlaygroundToken(accessToken);
    //     try {
    //         return playgroundClient.getPlaygroundUserIds(requestHeader, currentGeneration);
    //     } catch (BadRequest e) {
    //         throw new BadRequestException(ErrorCode.PLAYGROUND_PROFILE_NOT_EXISTS);
    //     } catch (ExpiredJwtException e) {
    //         throw new UnauthorizedException(ErrorCode.INVALID_PLAYGROUND_TOKEN);
    //     }
    // }

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

    // public PlaygroundPost getPlaygroundHotPost(String playgroundToken) {
    //     PlaygroundPostResponse postInfo =
    //             playgroundClient.getPlaygroundHotPost(createAuthorizationHeaderByUserPlaygroundToken(playgroundToken));
    //
    //     return PlaygroundPost.builder()
    //             .title(postInfo.title())
    //             .content(postInfo.content())
    //             .url(convertPlaygroundWebPageUrl(postInfo.postId()))
    //             .build();
    // }

    private String convertPlaygroundWebPageUrl(Long postId) {
        return playgroundWebPageUrl + "/?feed=" + postId;
    }

    // public boolean isCurrentGeneration(Long generation) {
    //     return generation.equals(currentGeneration);
    // }

    // public List<RecentPostsResponse> getRecentPosts(String token) {
    //     final Map<String, String> headers = createAuthorizationHeaderByUserPlaygroundToken(token);
    //
    //     try (ExecutorService executor = Executors.newVirtualThreadPerTaskExecutor()) {
    //         return collectHotAndCategoryPosts(headers, executor);
    //     }
    // }
    //
    // private List<RecentPostsResponse> collectHotAndCategoryPosts(Map<String, String> headers, ExecutorService executor) {
    //     CompletableFuture<RecentPostsResponse> hotPostFuture = getHotPost(headers, executor);
    //     List<CompletableFuture<RecentPostsResponse>> categoryFutures = getCategoryPosts(headers, executor);
    //
    //     List<CompletableFuture<RecentPostsResponse>> all = new ArrayList<>(categoryFutures);
    //     all.addFirst(hotPostFuture);
    //
    //     CompletableFuture<Void> allDone = CompletableFuture.allOf(all.toArray(new CompletableFuture[0]));
    //
    //     return allDone.thenApply(v -> all.stream()
    //         .map(CompletableFuture::join)
    //         .filter(Objects::nonNull)
    //         .toList()).join();
    // }
    //
    // private CompletableFuture<RecentPostsResponse> getHotPost(Map<String, String> headers, ExecutorService executor) {
    //     return CompletableFuture.supplyAsync(() -> {
    //         try {
    //             return Optional.ofNullable(playgroundClient.getPlaygroundHotPost(headers))
    //                 .map(post -> RecentPostsResponse.of(post, convertPlaygroundWebPageUrl(post.postId())))
    //                 .orElse(null);
    //         } catch (Exception e) {
    //             log.error("[HOT POST] 조회 실패", e);
    //             return null;
    //         }
    //     }, executor);
    // }
    //
    // private List<CompletableFuture<RecentPostsResponse>> getCategoryPosts(Map<String, String> headers, ExecutorService executor) {
    //     List<PlayGroundPostCategory> categories = List.of(
    //         PlayGroundPostCategory.SOPT_ACTIVITY,
    //         PlayGroundPostCategory.FREE,
    //         PlayGroundPostCategory.PART
    //     );

    //     return categories.stream()
    //         .map(category -> CompletableFuture.supplyAsync(() -> {
    //             try {
    //                 RecentPostsResponse response = playgroundClient.getRecentPosts(headers, category.getDisplayName());
    //                 if (response == null) return null;
    //                 String url = convertPlaygroundWebPageUrl(response.getId());
    //                 return response.withUrl(url);
    //             } catch (Exception e) {
    //                 log.error("[CATEGORY: {}] 게시물 조회 실패", category, e);
    //                 return null;
    //             }
    //         }, executor))
    //         .toList();
    // }

    // public List<RecentPostsResponse> getRecentPostsWithMemberInfo(String playgroundToken) {
    //     List<RecentPostsResponse> recentPosts = getRecentPosts(playgroundToken);
    //     return getPostsWithMemberInfo(playgroundToken, recentPosts);
    // }

    // public List<EmploymentPostResponse> getPlaygroundEmploymentPost(String accessToken) {
    //     Map<String, String> requestHeader = createAuthorizationHeaderByUserPlaygroundToken(accessToken);
    //     PlayGroundEmploymentResponse postInfo = playgroundClient.getPlaygroundEmploymentPost(requestHeader,16,10,0);
    //     return postInfo.posts().stream()
    //             .map(EmploymentPostResponse::of)
    //             .toList();
    // }

    // public List<CoffeeChatResponse> getCoffeeChatList(String accessToken) {
    //     Map<String, String> headers = PlaygroundHeaderCreator.createAuthorizationHeaderByUserPlaygroundToken(accessToken);
    //     return playgroundClient.getCoffeeChatList(headers).coffeeChatList().stream()
    //             .filter(member -> !member.isBlind())
    //             .map(i -> CoffeeChatResponse.of(i, getCurrentActivity(i)))
    //             .toList();
    // }
    //
    // public List<EmploymentPostResponse> getPlaygroundEmploymentPostWithMemberInfo(String playgroundToken) {
    //     List<EmploymentPostResponse> employmentPosts = getPlaygroundEmploymentPost(playgroundToken);
    //     return getPostsWithMemberInfo(playgroundToken, employmentPosts);
    // }

    // private <T extends PostWithMemberInfo> T addMemberInfoToPost(T post, PlayGroundPostDetailResponse postDetail) {
    //     if (postDetail.member() != null) {
    //         return (T) post.withMemberDetail(postDetail.member().name(), postDetail.member().profileImage());
    //     } else if (postDetail.anonymousProfile() != null) {
    //         return (T) post.withMemberDetail(postDetail.anonymousProfile().nickname(), postDetail.anonymousProfile().profileImgUrl());
    //     }
    //     throw new EntityNotFoundException("Member not found");
    // }
    //
    // private <T extends PostWithMemberInfo> List<T> getPostsWithMemberInfo(String playgroundToken, List<T> posts) {
    //     final Map<String, String> accessToken = createAuthorizationHeaderByUserPlaygroundToken(playgroundToken);
    //     List<T> mutablePosts = new ArrayList<>();
    //     for (T post : posts) {
    //         Long postId = post.getId();
    //         PlayGroundPostDetailResponse postDetail = playgroundClient.getPlayGroundPostDetail(accessToken, postId);
    //         mutablePosts.add(addMemberInfoToPost(post, postDetail));
    //     }
    //     return mutablePosts;
    // }

    // private String getCurrentActivity(PlayGroundCoffeeChatResponse playGroundCoffeeChatResponse) {
    //     return playGroundCoffeeChatResponse.soptActivities().stream()
    //             .filter(activity -> activity.contains(currentGeneration.toString()))
    //             .findFirst()
    //             .orElse(null);
    // }

    public int getUserSoptLevel(Long userId) {
        return playgroundClient.getPlayGroundUserSoptLevel(userId).soptProjectCount();
    }

    public PlaygroundProfile getPlayGroundProfile(Long userId) {
        return playgroundClient.getPlayGroundProfile(userId);
    }
    public List<PlaygroundRecentPost> getPlaygroundRecentPosts(Long userId) {
        return playgroundClient.getPlaygroundRecentPosts(userId);
    }

    public List<PlaygroundPopularPost> getPlaygroundPopularPosts(Long userId) {
        return playgroundClient.getPlaygroundPopularPosts(userId);
    }
}
