package org.sopt.app.facade;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.sopt.app.application.mission.MissionService;
import org.sopt.app.application.s3.S3Service;
import org.sopt.app.application.soptamp.SoptampPointService;
import org.sopt.app.application.soptamp.SoptampUserInfo;
import org.sopt.app.application.soptamp.SoptampUserService;
import org.sopt.app.application.stamp.StampInfo;
import org.sopt.app.application.stamp.StampService;

@ExtendWith(MockitoExtension.class)
class SoptampFacadeTest {

    @Mock
    private StampService stampService;
    @Mock
    private S3Service s3Service;
    @Mock
    private MissionService missionService;
    @Mock
    private SoptampUserService soptampUserService;
    @Mock
    private SoptampPointService soptampPointService;

    @InjectMocks
    private SoptampFacade soptampFacade;


    @Test
    @DisplayName("SUCCESS_스탬프 조회하기")
    void SUCCESS_getStampInfo() {
        // given
        final Long missionId = 1L;
        final Long userId = 1L;
        final String nickname = "anyNickname";
        final SoptampUserInfo.SoptampUser userInfo = SoptampUserInfo.SoptampUser.builder().userId(userId).nickname(nickname).build();
        final StampInfo.Stamp stampInfo = StampInfo.Stamp.builder().missionId(missionId).build();
        given(soptampUserService.findByNickname(nickname)).willReturn(userInfo);
        given(stampService.findStamp(missionId, userId)).willReturn(stampInfo);

        // when
        StampInfo.Stamp result = soptampFacade.getStampInfo(missionId, nickname);

        // then
        assertThat(result).usingRecursiveComparison().isEqualTo(stampInfo);
    }

    /* TODO: 아래 메서드 구현
    @Test
    void uploadStamp() {
    }

    @Test
    void uploadStampDeprecated() {
    }

    @Test
    void editStamp() {
    }

    @Test
    void deleteStamp() {
    }

    @Test
    void deleteStampAll() {
    }

    /* TODO: UserController 테스트 코드 작성하며 작성
    @Test
    void editSoptampUserNickname() {
    }

    @Test
    void editSoptampUserProfileMessage() {
    }
    */
}