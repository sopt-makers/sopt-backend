package org.sopt.app.facade;

import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.val;
import org.sopt.app.application.app_service.*;
import org.sopt.app.application.app_service.dto.*;
import org.sopt.app.application.home.ActivityDurationCalculator;
import org.sopt.app.application.meeting.*;
import org.sopt.app.application.playground.PlaygroundAuthService;
import org.sopt.app.application.description.DescriptionInfo.MainDescription;
import org.sopt.app.application.description.DescriptionService;
import org.sopt.app.domain.entity.User;
import org.sopt.app.domain.enums.UserStatus;
import org.sopt.app.presentation.home.response.HomeDescriptionResponse;
import org.sopt.app.presentation.home.MeetingParamRequest;
import org.sopt.app.presentation.home.response.RecentPostsResponse;
import org.sopt.app.presentation.home.response.EmploymentPostResponse;
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
        ActivityDurationCalculator calculator = ActivityDurationCalculator.of(ownGenerations);
        return HomeDescriptionResponse.of(
                user.getUsername(),
                calculator.getActivityDuration()
        );
    }

    public List<AppServiceEntryStatusResponse> checkAppServiceEntryStatus(User user) {

        return appServiceService.getAllAppService().stream()
                .filter(appServiceInfo -> isServiceVisibleToUser(appServiceInfo, user))
                .map(appServiceInfo -> appServiceBadgeService.getAppServiceEntryStatusResponse(
                        appServiceInfo, user.getId()
                )).toList();
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
        return playgroundAuthService.getRecentPosts(user.getPlaygroundToken()).stream()
                .map(post -> RecentPostsResponse.builder()
                        .id(post.getId())
                        .title(post.getTitle())
                        .category(post.getCategory())
                        .isHotPost(post.isHotPost())
                        .build()
                ).toList();
    }
  
    public List<EmploymentPostResponse> getHomeEmploymentPost(User  user) {
        return playgroundAuthService.getPlaygroundEmploymentPost(user.getPlaygroundToken());
    }

    public List<MeetingResponse> getAllMeetings(MeetingParamRequest request) {
        return meetingService.getAllMeetings(request)
                .meetings().stream()
                .filter(crewMeeting -> !crewMeeting.isBlockedMeeting())
                .map(MeetingResponse::of)
                .toList();
    }
}
