package org.sopt.app.facade;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.sopt.app.application.auth.PlaygroundAuthInfo;
import org.sopt.app.application.auth.PlaygroundAuthInfo.MainView;
import org.sopt.app.application.auth.PlaygroundAuthService;
import org.sopt.app.application.notification.NotificationService;
import org.sopt.app.application.operation.OperationInfo;
import org.sopt.app.domain.entity.User;
import org.sopt.app.domain.enums.UserStatus;
import org.sopt.app.presentation.user.UserResponse;
import org.sopt.app.presentation.user.UserResponse.Operation;
import org.sopt.app.presentation.user.UserResponseMapper;

@ExtendWith(MockitoExtension.class)
class UserFacadeTest {

    @Mock
    private PlaygroundAuthService playgroundAuthService;
    @Mock
    private NotificationService notificationService;
    @Mock
    private UserResponseMapper userResponseMapper;

    @InjectMocks
    private UserFacade userFacade;

    @Test
    @DisplayName("SUCCESS_메인 뷰 조회")
    void getMainViewInfo() {
        // given
        final boolean isNotificationConfirm = true;
        final PlaygroundAuthInfo.MainView playgroundAuthInfo = PlaygroundAuthInfo.MainView.builder().build();
        final UserResponse.Playground playground = UserResponse.Playground.builder().build();
        final UserResponse.Operation operation = Operation.builder().build();
        final UserResponse.MainView mainViewResponse = UserResponse.MainView.builder()
                .user(playground)
                .operation(operation)
                .isAllConfirm(isNotificationConfirm)
                .build();

        //when
        when(playgroundAuthService.getPlaygroundUserForMainView(anyString())).thenReturn(playgroundAuthInfo);
        when(notificationService.getNotificationConfirmStatus(any(User.class))).thenReturn(isNotificationConfirm);
        when(userResponseMapper.ofMainView(any(MainView.class), any(OperationInfo.MainView.class),
                anyBoolean())).thenReturn(mainViewResponse);

        UserResponse.MainView expected = UserResponse.MainView.builder()
                .user(mainViewResponse.getUser())
                .operation(mainViewResponse.getOperation())
                .isAllConfirm(mainViewResponse.getIsAllConfirm())
                .build();
        UserResponse.MainView result = userFacade.getMainViewInfo(
                User.builder()
                        .playgroundToken(anyString())
                        .build()
        );

        //then
        assertThat(result).usingRecursiveComparison().isEqualTo(expected);
    }
}