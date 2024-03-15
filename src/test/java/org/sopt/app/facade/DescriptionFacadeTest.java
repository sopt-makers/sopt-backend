package org.sopt.app.facade;

import static org.mockito.ArgumentMatchers.any;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.sopt.app.application.auth.PlaygroundAuthInfo.UserActiveInfo;
import org.sopt.app.application.auth.PlaygroundAuthService;
import org.sopt.app.application.description.DescriptionInfo;
import org.sopt.app.application.description.DescriptionInfo.MainDescription;
import org.sopt.app.application.description.DescriptionService;
import org.sopt.app.domain.entity.User;
import org.sopt.app.domain.enums.UserStatus;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class DescriptionFacadeTest {

    private static DescriptionService descriptionService = Mockito.mock(DescriptionService.class);
    private static PlaygroundAuthService playgroundAuthService = Mockito.mock(PlaygroundAuthService.class);

    private DescriptionFacade descriptionFacade = new DescriptionFacade(
            descriptionService,
            playgroundAuthService
    );

    @Test
    @DisplayName("SUCCESS - getMainDescriptionForUserSuccessActive")
    void success_getMainDescriptionForUserActive() {
        UserStatus userStatus = UserStatus.ACTIVE;
        Mockito.when(playgroundAuthService.getPlaygroundUserActiveInfo(any()))
                .thenReturn(UserActiveInfo.builder().currentGeneration(34L).status(userStatus).build());
        Mockito.when(descriptionService.getMainDescription(userStatus))
                .thenReturn(DescriptionInfo.MainDescription.builder().topDescription("activeTop")
                        .bottomDescription("activeBottom").build());

        MainDescription result = descriptionFacade.getMainDescriptionForUser(new User());
        Assertions.assertEquals("activeTop", result.getTopDescription());
        Assertions.assertEquals("activeBottom", result.getBottomDescription());
    }

    @Test
    @DisplayName("SUCCESS - getMainDescriptionForUserSuccessInactive")
    void success_getMainDescriptionForUserInactive() {
        UserStatus userStatus = UserStatus.INACTIVE;
        Mockito.when(playgroundAuthService.getPlaygroundUserActiveInfo(any()))
                .thenReturn(UserActiveInfo.builder().currentGeneration(29L).status(userStatus).build());
        Mockito.when(descriptionService.getMainDescription(userStatus))
                .thenReturn(DescriptionInfo.MainDescription.builder().topDescription("inactiveTop")
                        .bottomDescription("inactiveBottom").build());

        MainDescription result = descriptionFacade.getMainDescriptionForUser(new User());
        Assertions.assertEquals("inactiveTop", result.getTopDescription());
        Assertions.assertEquals("inactiveBottom", result.getBottomDescription());
    }
}
