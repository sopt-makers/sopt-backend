package org.sopt.app.facade;

import static org.sopt.app.common.utils.HtmlTagWrapper.wrapWithTag;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.sopt.app.application.appservice.AppServiceBadgeService;
import org.sopt.app.application.appservice.AppServiceName;
import org.sopt.app.application.appservice.AppServiceService;
import org.sopt.app.application.appservice.OperationConfigService;
import org.sopt.app.application.appservice.dto.AppServiceEntryStatusResponse;
import org.sopt.app.application.appservice.dto.AppServiceInfo;
import org.sopt.app.application.description.DescriptionService;
import org.sopt.app.application.meeting.MeetingResponse;
import org.sopt.app.application.meeting.MeetingService;
import org.sopt.app.application.platform.PlatformService;
import org.sopt.app.application.platform.dto.PlatformUserInfoResponse;
import org.sopt.app.application.playground.PlaygroundPostCacheService;
import org.sopt.app.application.playground.PlaygroundPopularPostRefreshEvent;
import org.sopt.app.application.playground.PlaygroundRecentPostRefreshEvent;
import org.sopt.app.application.playground.dto.PlaygroundPopularPost;
import org.sopt.app.application.playground.dto.PlaygroundRecentPost;
import org.sopt.app.application.soptamp.SoptampUserService;
import org.sopt.app.common.config.OperationConfig;
import org.sopt.app.common.config.OperationConfigCategory;
import org.sopt.app.common.event.EventPublisher;
import org.sopt.app.common.utils.ActivityDurationCalculator;
import org.sopt.app.domain.enums.UserStatus;
import org.sopt.app.presentation.home.MeetingParamRequest;
import org.sopt.app.presentation.home.response.FloatingButtonResponse;
import org.sopt.app.presentation.home.response.HomeDescriptionResponse;
import org.sopt.app.presentation.home.response.ReviewFormResponse;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class HomeFacade {

    private final DescriptionService descriptionService;
    private final PlaygroundPostCacheService playgroundPostCacheService;
    private final AppServiceService appServiceService;
    private final AppServiceBadgeService appServiceBadgeService;
    private final MeetingService meetingService;
    private final OperationConfigService operationConfigService;
    private final PlatformService platformService;
    private final SoptampUserService soptampUserService;
    private final EventPublisher eventPublisher;

    // TODO : deprecated 된것으로 인지
//    @Transactional(readOnly = true)
//    @Deprecated
//    public MainDescription getMainDescriptionForUser(User user) {
//        val userActiveInfo = playgroundAuthService.getPlaygroundUserActiveInfo(user.getPlaygroundToken(),
//                user.getPlaygroundId());
//        return descriptionService.getMainDescription(userActiveInfo.status());
//    }

    public HomeDescriptionResponse getHomeMainDescription(Long userId) {
        PlatformUserInfoResponse platformUserInfoResponse = platformService.getPlatformUserInfoResponse(userId);
        int duration = ActivityDurationCalculator.calculate(platformService.getMemberGenerationList(platformUserInfoResponse));
        return HomeDescriptionResponse.of(
                wrapWithTag(platformUserInfoResponse.name(), "b"),
                duration
        );
    }

    public List<AppServiceEntryStatusResponse> checkAppServiceEntryStatus(Long userId) {
        if(userId == null){
            return this.getOnlyAppServiceInfo();
        }
        // TODO : 추후 유저 생성 api response 변경해 생성 api 쪽에서 soptamp user upsert 하도록 변경
        PlatformUserInfoResponse platformUserInfo = platformService.getPlatformUserInfoResponse(userId);
        UserStatus status = platformService.getStatus(platformUserInfo);
        soptampUserService.upsertSoptampUser(platformUserInfo, userId);

        List<AppServiceEntryStatusResponse> appServiceEntryStatusResponses = appServiceService.getAllAppService().stream()
            .filter(appServiceInfo -> isServiceVisibleToUser(appServiceInfo, status))
            .map(appServiceInfo -> appServiceBadgeService.getAppServiceEntryStatusResponse(
                appServiceInfo, userId
            ))
            .toList();
        return appServiceEntryStatusResponses;
    }

    private List<AppServiceEntryStatusResponse> getOnlyAppServiceInfo() {
        return appServiceService.getAllAppService().stream()
                .map(AppServiceEntryStatusResponse::createOnlyAppServiceInfo)
                .toList();
    }

    private boolean isServiceVisibleToUser(AppServiceInfo appServiceInfo, UserStatus status) {
        if (appServiceInfo == null || status == null) return false;
        if (status == UserStatus.ACTIVE) {
            return appServiceInfo.getActiveUser();
        }
        if (status == UserStatus.INACTIVE) {
            return appServiceInfo.getInactiveUser();
        }
        return false;
    }

    // public List<RecentPostsResponse> getRecentPosts(Long userId) {
    //
    //     return playgroundAuthService.getRecentPostsWithMemberInfo(user.getPlaygroundToken());
    // }

//    public List<EmploymentPostResponse> getHomeEmploymentPost(User user) {
//        return playgroundAuthService.getPlaygroundEmploymentPostWithMemberInfo(user.getPlaygroundToken());
//    }
//
//    @Transactional(readOnly = true)
//    public List<CoffeeChatResponse> getCoffeeChatList(User user) {
//        return playgroundAuthService.getCoffeeChatList(user.getPlaygroundToken());
//    }

    public List<MeetingResponse> getAllMeetings(MeetingParamRequest request) {
        return meetingService.getAllMeetings(request)
                .meetings().stream()
                .filter(crewMeeting -> !crewMeeting.isBlockedMeeting())
                .map(MeetingResponse::of)
                .toList();
    }

    public FloatingButtonResponse getFloatingButtonInfo(Long userId) {
        boolean isActive = false;
        if (userId != null) {
            UserStatus userStatus = platformService.getStatus(userId);
            isActive = userStatus == UserStatus.ACTIVE ?
                    appServiceService.getAppService(AppServiceName.FLOATING_BUTTON.getServiceName()).getActiveUser() :
                    appServiceService.getAppService(AppServiceName.FLOATING_BUTTON.getServiceName()).getInactiveUser();
        }

        Map<String, String> operationConfigMap = operationConfigService.getOperationConfigByOperationConfigType(OperationConfigCategory.FLOATING_BUTTON).stream()
                .collect(Collectors.toMap(OperationConfig::getKey, OperationConfig::getValue));

        return FloatingButtonResponse.of(
                operationConfigMap.get("imageUrl"),
                operationConfigMap.get("title"),
                operationConfigMap.get("expandedSubTitle"),
                operationConfigMap.get("collapsedSubtitle"),
                operationConfigMap.get("actionButtonName"),
                operationConfigMap.get("linkUrl"),
                isActive
        );

    }

    public ReviewFormResponse getReviewFormInfo(Long userId) {
        boolean isActive = false;
        if (userId != null) {
            UserStatus userStatus = platformService.getStatus(userId);
            isActive = userStatus == UserStatus.ACTIVE ?
                appServiceService.getAppService(AppServiceName.REVIEW_FORM.getServiceName()).getActiveUser() :
                appServiceService.getAppService(AppServiceName.REVIEW_FORM.getServiceName()).getInactiveUser();
        }

        Map<String, String> operationConfigMap = operationConfigService
            .getOperationConfigByOperationConfigType(OperationConfigCategory.REVIEW_FORM).stream()
            .collect(Collectors.toMap(OperationConfig::getKey, OperationConfig::getValue));

        return ReviewFormResponse.of(
                operationConfigMap.get("title"),
                operationConfigMap.get("subTitle"),
                operationConfigMap.get("actionButtonName"),
                operationConfigMap.get("linkUrl"),
                isActive
        );
    }

    public List<PlaygroundRecentPost> getPlaygroundRecentPosts(Long userId) {
        Optional<List<PlaygroundRecentPost>> cachedPosts = playgroundPostCacheService.getCachedRecentPosts();
        if (cachedPosts.isPresent()) {
            return cachedPosts.get();
        }

        eventPublisher.raise(new PlaygroundRecentPostRefreshEvent());
        return List.of();
    }

    public List<PlaygroundPopularPost> getPlaygroundPopularPosts(Long userId) {
        Optional<List<PlaygroundPopularPost>> cachedPosts = playgroundPostCacheService.getCachedPopularPosts();
        if (cachedPosts.isPresent()) {
            return cachedPosts.get();
        }

        eventPublisher.raise(new PlaygroundPopularPostRefreshEvent());
        return List.of();
    }
}
