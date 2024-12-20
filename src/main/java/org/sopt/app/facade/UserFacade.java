package org.sopt.app.facade;

import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.val;
import org.sopt.app.application.playground.PlaygroundAuthService;
import org.sopt.app.application.notification.NotificationService;
import org.sopt.app.application.operation.OperationInfo;
import org.sopt.app.application.app_service.AppServiceService;
import org.sopt.app.domain.entity.User;
import org.sopt.app.presentation.user.UserResponse.AppService;
import org.sopt.app.presentation.user.UserResponse.MainView;
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
        val mainViewUser = playgroundAuthService.getPlaygroundUserForMainView(user.getPlaygroundToken(),
                user.getPlaygroundId());
        val dummyOperation = OperationInfo.MainView.builder().announcement("공지다!").attendanceScore(2D).build();
        val mainViewNotification = notificationService.getNotificationConfirmStatus(user.getId());
        return userResponseMapper.ofMainView(mainViewUser, dummyOperation, mainViewNotification);
    }

    @Transactional(readOnly = true)
    @Deprecated
    public List<AppService> getAppServiceInfo() {
        return appServiceService.getAllAppService().stream()
                .map(AppService::of)
                .toList();
    }
}
