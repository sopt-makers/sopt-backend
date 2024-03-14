package org.sopt.app.application;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.sopt.app.application.description.DescriptionInfo;
import org.sopt.app.application.description.DescriptionService;
import org.sopt.app.domain.enums.UserStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class DescriptionServiceTest {

    @Autowired
    private DescriptionService descriptionService;

    @Test
    @DisplayName("200 - getMainDescriptionSuccessActive")
    void getMainDescriptionSuccessActive() {
        DescriptionInfo.MainDescription result = descriptionService.getMainDescription(UserStatus.ACTIVE);
        Assertions.assertEquals("33기 DO SOPT, 지금 출발합니다!", result.getTopDescription());
        Assertions.assertEquals("벋벋조와 함께하는 도장찍기!", result.getBottomDescription());
    }

    @Test
    @DisplayName("200 - getMainDescriptionSuccessInActive")
    void getMainDescriptionSuccessInactive() {
        DescriptionInfo.MainDescription result = descriptionService.getMainDescription(UserStatus.INACTIVE);
        Assertions.assertEquals("더욱 편리해진 SOPT를 이용해보세요!", result.getTopDescription());
        Assertions.assertEquals("SOPT를 알차게 즐기고 싶다면?", result.getBottomDescription());
    }
}
