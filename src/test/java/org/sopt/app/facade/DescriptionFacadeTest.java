package org.sopt.app.facade;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.sopt.app.application.auth.dto.PlaygroundProfileInfo.UserActiveInfo;
import org.sopt.app.application.auth.PlaygroundAuthService;
import org.sopt.app.application.description.DescriptionInfo;
import org.sopt.app.application.description.DescriptionInfo.MainDescription;
import org.sopt.app.application.description.DescriptionService;
import org.sopt.app.domain.entity.User;
import org.sopt.app.domain.enums.UserStatus;

@ExtendWith(MockitoExtension.class)
class DescriptionFacadeTest {

    @Mock
    private DescriptionService descriptionService;

    @Mock
    private PlaygroundAuthService playgroundAuthService;

    @InjectMocks
    private DescriptionFacade descriptionFacade;

    @Test
    @DisplayName("SUCCESS_활동 유저 메인 문구 조회")
    void SUCCESS_getMainDescriptionForUserActive() {
        User user = User.builder().playgroundId(1L).playgroundToken("token").build();
        UserStatus userStatus = UserStatus.ACTIVE;
        Mockito.when(playgroundAuthService.getPlaygroundUserActiveInfo(anyString(), anyLong()))
                .thenReturn(UserActiveInfo.builder().currentGeneration(34L).status(userStatus).build());
        Mockito.when(descriptionService.getMainDescription(userStatus))
                .thenReturn(DescriptionInfo.MainDescription.builder().topDescription("activeTop")
                        .bottomDescription("activeBottom").build());

        MainDescription result = descriptionFacade.getMainDescriptionForUser(user);
        Assertions.assertEquals("activeTop", result.getTopDescription());
        Assertions.assertEquals("activeBottom", result.getBottomDescription());
    }

    @Test
    @DisplayName("SUCCESS_비활동 유저 메인 문구 조회")
    void SUCCESS_getMainDescriptionForUserInactive() {
        User user = User.builder().playgroundId(1L).playgroundToken("token").build();
        UserStatus userStatus = UserStatus.INACTIVE;
        Mockito.when(playgroundAuthService.getPlaygroundUserActiveInfo(anyString(), anyLong()))
                .thenReturn(UserActiveInfo.builder().currentGeneration(29L).status(userStatus).build());
        Mockito.when(descriptionService.getMainDescription(userStatus))
                .thenReturn(DescriptionInfo.MainDescription.builder().topDescription("inactiveTop")
                        .bottomDescription("inactiveBottom").build());

        MainDescription result = descriptionFacade.getMainDescriptionForUser(user);
        Assertions.assertEquals("inactiveTop", result.getTopDescription());
        Assertions.assertEquals("inactiveBottom", result.getBottomDescription());
    }
}
