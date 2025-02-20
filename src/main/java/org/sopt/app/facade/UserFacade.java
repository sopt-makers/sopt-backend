package org.sopt.app.facade;

import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.val;
import org.sopt.app.application.playground.PlaygroundAuthService;
import org.sopt.app.application.notification.NotificationService;
import org.sopt.app.application.app_service.AppServiceService;
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

    @Transactional(readOnly = true)
    public MainView getMainViewInfo(User user) {
        if(user == null) {
            return MainView.unauthenticatedMainView();
        }

        val mainViewUser = playgroundAuthService.getPlaygroundUserForMainView(
                user.getPlaygroundToken(), user.getPlaygroundId()
        );
        val mainViewNotification = notificationService.getNotificationConfirmStatus(user.getId());
        return userResponseMapper.ofMainView(mainViewUser, Operation.defaultOperation(), mainViewNotification);
    }

    @Transactional(readOnly = true)
    @Deprecated
    public List<AppService> getAppServiceInfo() {
        return appServiceService.getAllAppService().stream()
                .map(AppService::of)
                .toList();
    }
}
