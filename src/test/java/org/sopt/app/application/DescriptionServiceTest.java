package org.sopt.app.application;

import static org.mockito.Mockito.when;

import java.util.List;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.sopt.app.application.description.DescriptionInfo;
import org.sopt.app.application.description.DescriptionService;
import org.sopt.app.domain.entity.MainDescription;
import org.sopt.app.domain.enums.UserStatus;
import org.sopt.app.interfaces.postgres.MainDescriptionRepository;

@ExtendWith(MockitoExtension.class)
public class DescriptionServiceTest {

    @Mock
    private MainDescriptionRepository mainDescriptionRepository;

    @InjectMocks
    private DescriptionService descriptionService;

    @BeforeEach
    private void beforeTest() {
        when(mainDescriptionRepository.findAll()).thenReturn(List.of(
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
    @DisplayName("SUCCESS_활동 유저 메인 문구 조회")
    void SUCCESS_getMainDescriptionActive() {
        DescriptionInfo.MainDescription result = descriptionService.getMainDescription(UserStatus.ACTIVE);

        Assertions.assertEquals("activeTop", result.getTopDescription());
        Assertions.assertEquals("activeBottom", result.getBottomDescription());
    }

    @Test
    @DisplayName("SUCCESS_비활동 유저 메인 문구 조회")
    void SUCCESS_getMainDescriptionInactive() {
        DescriptionInfo.MainDescription result = descriptionService.getMainDescription(UserStatus.INACTIVE);

        Assertions.assertEquals("inactiveTop", result.getTopDescription());
        Assertions.assertEquals("inactiveBottom", result.getBottomDescription());
    }

    @Test
    @DisplayName("SUCCESS_미인증 유저 메인 문구 조회")
    void SUCCESS_getMainDescriptionUnauthenticated() {
        DescriptionInfo.MainDescription result = descriptionService.getMainDescription(UserStatus.UNAUTHENTICATED);

        Assertions.assertEquals("inactiveTop", result.getTopDescription());
        Assertions.assertEquals("inactiveBottom", result.getBottomDescription());
    }
}
