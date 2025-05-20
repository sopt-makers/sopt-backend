package org.sopt.app.facade;

import static org.sopt.app.common.utils.HtmlTagWrapper.wrapWithTag;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import lombok.RequiredArgsConstructor;
import lombok.val;
import org.sopt.app.application.app_service.*;
import org.sopt.app.application.app_service.dto.*;
import org.sopt.app.application.description.DescriptionInfo.MainDescription;
import org.sopt.app.application.description.DescriptionService;
import org.sopt.app.common.config.OperationConfig;
import org.sopt.app.common.config.OperationConfigCategory;
import org.sopt.app.common.utils.ActivityDurationCalculator;
import org.sopt.app.application.meeting.*;
import org.sopt.app.application.playground.PlaygroundAuthService;
import org.sopt.app.domain.entity.User;
import org.sopt.app.domain.enums.UserStatus;
import org.sopt.app.presentation.home.response.*;
import org.sopt.app.presentation.home.MeetingParamRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class HomeFacade {

    private final DescriptionService descriptionService;
    private final PlaygroundAuthService playgroundAuthService;
    private final AppServiceService appServiceService;
    private final AppServiceBadgeService appServiceBadgeService;
    private final MeetingService meetingService;
    private final OperationConfigService operationConfigService;

    @Transactional(readOnly = true)
    @Deprecated
    public MainDescription getMainDescriptionForUser(User user) {
        val userActiveInfo = playgroundAuthService.getPlaygroundUserActiveInfo(user.getPlaygroundToken(),
                user.getPlaygroundId());
        return descriptionService.getMainDescription(userActiveInfo.status());
    }

    @Transactional(readOnly = true)
    public HomeDescriptionResponse getHomeMainDescription(User user) {
        List<Long> ownGenerations = playgroundAuthService.getOwnPlaygroundProfile(user.getPlaygroundToken())
                .getAllGenerations();
        int duration = ActivityDurationCalculator.calculate(ownGenerations);
        return HomeDescriptionResponse.of(
                wrapWithTag(user.getUsername(), "b"),
                duration
        );
    }
    @Transactional(readOnly = true)
    public List<AppServiceEntryStatusResponse> checkAppServiceEntryStatus(User user) {
        if(user == null){
            return this.getOnlyAppServiceInfo();
        }
        
        return appServiceService.getAllAppService().stream()
                .filter(appServiceInfo -> isServiceVisibleToUser(appServiceInfo, user))
                .map(appServiceInfo -> appServiceBadgeService.getAppServiceEntryStatusResponse(
                        appServiceInfo, user.getId()
                ))
                .toList();
    }

    private List<AppServiceEntryStatusResponse> getOnlyAppServiceInfo() {
        return appServiceService.getAllAppService().stream()
                .map(AppServiceEntryStatusResponse::createOnlyAppServiceInfo)
                .toList();
    }

    private boolean isServiceVisibleToUser(AppServiceInfo appServiceInfo, User user) {
        UserStatus status = playgroundAuthService.getPlaygroundUserActiveInfo(
                user.getPlaygroundToken(), user.getPlaygroundId()
        ).status();

        if (status == UserStatus.ACTIVE) {
            return appServiceInfo.getActiveUser();
        }
        if (status == UserStatus.INACTIVE) {
            return appServiceInfo.getInactiveUser();
        }

        return false;
    }

    public List<RecentPostsResponse> getRecentPosts(User user) {
        return playgroundAuthService.getRecentPostsWithMemberInfo(user.getPlaygroundToken());
    }

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
    public FloatingButtonResponse getFloatingButtonInfo(User user) {
        boolean isActive = false;
        if (user != null) {
            UserStatus userStatus = playgroundAuthService.getPlaygroundUserActiveInfo(
                    user.getPlaygroundToken(),
                    user.getPlaygroundId()
            ).status();

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
    public ReviewFormResponse getReviewFormInfo(User user) {
        boolean isActive = true;
        if (user == null) isActive = false;
        Map<String, String> operationConfigMap = operationConfigService.getOperationConfigByOperationConfigType(OperationConfigCategory.REVIEW_FORM).stream()
            .collect(Collectors.toMap(OperationConfig::getKey, OperationConfig::getValue));

        return ReviewFormResponse.of(
                operationConfigMap.get("title"),
                operationConfigMap.get("subTitle"),
                operationConfigMap.get("actionButtonName"),
                operationConfigMap.get("linkUrl"),
                isActive
        );
    }
}
