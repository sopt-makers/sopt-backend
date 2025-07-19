package org.sopt.app.facade;

import static org.sopt.app.common.utils.HtmlTagWrapper.*;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.sopt.app.application.app_service.AppServiceBadgeService;
import org.sopt.app.application.app_service.AppServiceName;
import org.sopt.app.application.app_service.AppServiceService;
import org.sopt.app.application.app_service.OperationConfigService;
import org.sopt.app.application.app_service.dto.AppServiceEntryStatusResponse;
import org.sopt.app.application.description.DescriptionInfo.MainDescription;
import org.sopt.app.application.description.DescriptionService;
import org.sopt.app.application.meeting.MeetingResponse;
import org.sopt.app.application.meeting.MeetingService;
import org.sopt.app.application.platform.PlatformService;
import org.sopt.app.application.playground.PlaygroundAuthService;
import org.sopt.app.application.playground.dto.PlaygroundPopularPost;
import org.sopt.app.application.playground.dto.PlaygroundRecentPost;
import org.sopt.app.common.config.OperationConfig;
import org.sopt.app.common.config.OperationConfigCategory;
import org.sopt.app.common.utils.ActivityDurationCalculator;
import org.sopt.app.domain.entity.User;
import org.sopt.app.domain.enums.UserStatus;
import org.sopt.app.presentation.home.MeetingParamRequest;
import org.sopt.app.presentation.home.response.CoffeeChatResponse;
import org.sopt.app.presentation.home.response.EmploymentPostResponse;
import org.sopt.app.presentation.home.response.FloatingButtonResponse;
import org.sopt.app.presentation.home.response.HomeDescriptionResponse;
import org.sopt.app.presentation.home.response.ReviewFormResponse;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import lombok.val;

@Service
@RequiredArgsConstructor
public class HomeFacade {

    private final DescriptionService descriptionService;
    private final PlaygroundAuthService playgroundAuthService;
    private final AppServiceService appServiceService;
    private final AppServiceBadgeService appServiceBadgeService;
    private final MeetingService meetingService;
    private final OperationConfigService operationConfigService;
    private final PlatformService platformService;

    // TODO : deprecated 된것으로 인지
    @Transactional(readOnly = true)
    @Deprecated
    public MainDescription getMainDescriptionForUser(User user) {
        val userActiveInfo = playgroundAuthService.getPlaygroundUserActiveInfo(user.getPlaygroundToken(),
                user.getPlaygroundId());
        return descriptionService.getMainDescription(userActiveInfo.status());
    }

    @Transactional(readOnly = true)
    public HomeDescriptionResponse getHomeMainDescription(Long userId) {
        int duration = ActivityDurationCalculator.calculate(platformService.getMemberGenerationList(userId));
        return HomeDescriptionResponse.of(
                wrapWithTag(platformService.getPlatformUserInfoResponse(userId).name(), "b"),
                duration
        );
    }
    @Transactional(readOnly = true)
    public List<AppServiceEntryStatusResponse> checkAppServiceEntryStatus(Long userId) {
        if(userId == null){
            return this.getOnlyAppServiceInfo();
        }
        UserStatus status = platformService.getStatus(userId);

        return appServiceService.getAllAppService().stream()
                .filter(appServiceInfo -> isServiceVisibleToUser(status))
                .map(appServiceInfo -> appServiceBadgeService.getAppServiceEntryStatusResponse(
                        appServiceInfo, userId
                ))
                .toList();
    }

    private List<AppServiceEntryStatusResponse> getOnlyAppServiceInfo() {
        return appServiceService.getAllAppService().stream()
                .map(AppServiceEntryStatusResponse::createOnlyAppServiceInfo)
                .toList();
    }

    private boolean isServiceVisibleToUser(UserStatus status) {
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

    public List<EmploymentPostResponse> getHomeEmploymentPost(User user) {
        return playgroundAuthService.getPlaygroundEmploymentPostWithMemberInfo(user.getPlaygroundToken());
    }

    @Transactional(readOnly = true)
    public List<CoffeeChatResponse> getCoffeeChatList(User user) {
        return playgroundAuthService.getCoffeeChatList(user.getPlaygroundToken());
    }

    public List<MeetingResponse> getAllMeetings(MeetingParamRequest request) {
        return meetingService.getAllMeetings(request)
                .meetings().stream()
                .filter(crewMeeting -> !crewMeeting.isBlockedMeeting())
                .map(MeetingResponse::of)
                .toList();
    }

    @Transactional(readOnly = true)
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

    @Transactional(readOnly = true)
    public ReviewFormResponse getReviewFormInfo(Long userId) {
        boolean isActive = true;
        if (userId == null) isActive = false;
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

    @Transactional(readOnly = true)
    public List<PlaygroundRecentPost> getPlaygroundRecentPosts(User user) {
        List<OperationConfig> configList = operationConfigService.getOperationConfigByOperationConfigType(OperationConfigCategory.PLAYGROUND_POST);
        Map<String, String> imageConfigMap = PlaygroundRecentPost.toImageConfigMap(configList);

        return playgroundAuthService.getPlaygroundRecentPosts(user.getPlaygroundToken()).stream()
            .map(post -> PlaygroundRecentPost.from(
                post.playgroundPostId(),
                post.profileImage(),
                post.name(),
                post.generationAndPart(),
                post.category(),
                post.title(),
                post.content(),
                post.webLink(),
                post.createdAt(),
                imageConfigMap
            ))
            .toList();
    }

    @Transactional(readOnly = true)
    public List<PlaygroundPopularPost> getPlaygroundPopularPosts(User user) {
        return playgroundAuthService.getPlaygroundPopularPosts(user.getPlaygroundToken());
    }
}
