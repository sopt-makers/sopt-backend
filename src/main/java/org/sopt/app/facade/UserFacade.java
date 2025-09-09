package org.sopt.app.facade;

import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.val;

import org.sopt.app.application.platform.PlatformService;
import org.sopt.app.application.platform.dto.PlatformUserInfoResponse;
import org.sopt.app.application.playground.PlaygroundAuthService;
import org.sopt.app.application.notification.NotificationService;
import org.sopt.app.application.app_service.AppServiceService;
import org.sopt.app.application.playground.dto.PlaygroundProfileInfo;
import org.sopt.app.application.user.UserInfo;
import org.sopt.app.application.user.UserService;
import org.sopt.app.domain.entity.User;
import org.sopt.app.presentation.user.UserResponse.*;
import org.sopt.app.presentation.user.UserResponseMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UserFacade {

    private final PlaygroundAuthService playgroundAuthService;
    private final NotificationService notificationService;
    private final AppServiceService appServiceService;
    private final UserResponseMapper userResponseMapper;
    private final PlatformService platformService;
    private final UserService userService;

    @Transactional(readOnly = true)
    public MainView getMainViewInfo(Long userId) {
        if(userId == null) {
            return MainView.unauthenticatedMainView();
        }
        PlatformUserInfoResponse platformUserInfoResponse = platformService.getPlatformUserInfoResponse(userId);
        PlaygroundProfileInfo.MainViewUser mainViewUser = PlaygroundProfileInfo.MainViewUser.builder()
            .name(platformUserInfoResponse.name())
            .status(platformService.getStatus(userId))
            .profileImage(platformUserInfoResponse.profileImage())
            .generationList(platformService.getMemberGenerationList(userId).stream().toList())
            .build();

        boolean mainViewNotification = notificationService.getNotificationConfirmStatus(userId);
        return userResponseMapper.ofMainView(PlaygroundProfileInfo.MainView.of(mainViewUser), Operation.defaultOperation(), mainViewNotification);
    }

    @Transactional(readOnly = true)
    @Deprecated
    public List<AppService> getAppServiceInfo() {
        return appServiceService.getAllAppService().stream()
                .map(AppService::of)
                .toList();
    }

    @Transactional
    public UserInfo createUser(Long requestUserId){
        return userService.createUser(requestUserId);
    }
}
