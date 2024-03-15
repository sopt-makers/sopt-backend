package org.sopt.app.application;

import java.util.List;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.sopt.app.application.description.DescriptionInfo;
import org.sopt.app.application.description.DescriptionService;
import org.sopt.app.domain.entity.MainDescription;
import org.sopt.app.domain.enums.UserStatus;
import org.sopt.app.interfaces.postgres.MainDescriptionRepository;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class DescriptionServiceTest {

    private static MainDescriptionRepository mainDescriptionRepository = Mockito.mock(MainDescriptionRepository.class);

    private DescriptionService descriptionService = new DescriptionService(
            mainDescriptionRepository
    );

    @BeforeAll
    private static void beforeTest() {
        Mockito.when(mainDescriptionRepository.findAll()).thenReturn(List.of(
                MainDescription.builder()
                        .id(1L)
                        .activeTopDescription("activeTop")
                        .activeBottomDescription("activeBottom")
                        .inactiveTopDescription("inactiveTop")
                        .inactiveBottomDescription("inactiveBottom")
                        .build()
        ));
    }

    @Test
    @DisplayName("SUCCESS - getMainDescriptionSuccessActive")
    void getMainDescriptionSuccessActive() {
        DescriptionInfo.MainDescription result = descriptionService.getMainDescription(UserStatus.ACTIVE);
        Assertions.assertEquals("activeTop", result.getTopDescription());
        Assertions.assertEquals("activeBottom", result.getBottomDescription());
    }

    @Test
    @DisplayName("SUCCESS - getMainDescriptionSuccessInactive")
    void getMainDescriptionSuccessInactive() {
        DescriptionInfo.MainDescription result = descriptionService.getMainDescription(UserStatus.INACTIVE);
        Assertions.assertEquals("inactiveTop", result.getTopDescription());
        Assertions.assertEquals("inactiveBottom", result.getBottomDescription());
    }

    @Test
    @DisplayName("SUCCESS - getMainDescriptionSuccessUnauthenticated")
    void getMainDescriptionSuccessUnauthenticated() {
        DescriptionInfo.MainDescription result = descriptionService.getMainDescription(UserStatus.UNAUTHENTICATED);
        Assertions.assertEquals("inactiveTop", result.getTopDescription());
        Assertions.assertEquals("inactiveBottom", result.getBottomDescription());
    }
}
