package org.sopt.app.facade;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.sopt.app.common.fixtures.SoptampFixture.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.sopt.app.application.mission.*;
import org.sopt.app.application.soptamp.*;
import org.sopt.app.application.stamp.*;
import org.sopt.app.common.fixtures.SoptampFixture;
import org.sopt.app.domain.entity.soptamp.Stamp;
import org.sopt.app.presentation.stamp.StampRequest;

@ExtendWith(MockitoExtension.class)
class SoptampFacadeTest {

    @Mock
    private StampService stampService;
    @Mock
    private MissionService missionService;
    @Mock
    private SoptampUserService soptampUserService;
    @Mock
    private SoptampUserFinder soptampUserFinder;

    @InjectMocks
    private SoptampFacade soptampFacade;


    @Test
    @DisplayName("SUCCESS_스탬프 조회하기")
    void SUCCESS_getStampInfo() {
        // given
        final StampInfo.Stamp stampInfo = SoptampFixture.getStampInfo();
        given(soptampUserFinder.findByNickname(NICKNAME)).willReturn(SoptampFixture.getUserInfo());
        given(stampService.findStamp(MISSION_ID, USER_ID)).willReturn(stampInfo);

        // when
        StampInfo.Stamp result = soptampFacade.getStampInfo(MISSION_ID, NICKNAME);

        // then
        assertEquals(stampInfo, result);
    }


    @Test
    @DisplayName("SUCCESS_스탬프 업로드하기")
    void SUCCESS_uploadStamp() {
        // given
        final StampInfo.Stamp uploadedStamp = SoptampFixture.getStampInfo();
        final StampRequest.RegisterStampRequest registerStampRequest = SoptampFixture.getRegisterStampRequest();
        given(stampService.uploadStamp(registerStampRequest, SOPTAMP_USER_ID)).willReturn(uploadedStamp);
        given(missionService.getMissionById(MISSION_ID)).willReturn(MissionInfo.Level.of(MISSION_LEVEL));

        // when
        StampInfo.Stamp result = soptampFacade.uploadStamp(SOPTAMP_USER_ID, registerStampRequest);

        // then
        assertEquals(uploadedStamp, result);
    }

    @Test
    @DisplayName("SUCCESS_스탬프 삭제하기")
    void SUCCESS_deleteStamp() {
        // given
        Stamp stamp = Stamp.builder()
            .id(STAMP_ID)
            .userId(SOPTAMP_USER_ID)
            .missionId(MISSION_ID)
            .images(STAMP_IMG_PATHS)
            .contents(STAMP_CONTENTS)
            .activityDate(STAMP_ACTIVITY_DATE)
            .build();
        given(stampService.getStampForDelete(STAMP_ID, SOPTAMP_USER_ID)).willReturn(stamp);
        given(missionService.getMissionById(MISSION_ID)).willReturn(MissionInfo.Level.of(MISSION_LEVEL));

        // when
        soptampFacade.deleteStamp(SOPTAMP_USER_ID, STAMP_ID);

        // then
        then(stampService).should().deleteStampById(STAMP_ID);
    }

    @Test
    @DisplayName("SUCCESS_모든 스탬프 삭제하기")
    void SUCCESS_deleteStampAll() {
        // given & when
        soptampFacade.deleteStampAll(SOPTAMP_USER_ID);

        // then
        then(stampService).should().deleteAllStamps(SOPTAMP_USER_ID);
        then(soptampUserService).should().initPoint(SOPTAMP_USER_ID);
    }

    @Test
    @DisplayName("SUCCESS_솝탬프 유저 프로필 메시지 수정하기")
    void SUCCESS_editSoptampUserProfileMessage() {
        // given
        final SoptampUserInfo soptampUser = SoptampFixture.getUserInfo();
        final String newProfileMessage = "new message";

        // when
        soptampFacade.editSoptampUserProfileMessage(USER_ID, newProfileMessage);

        // then
        then(soptampUserService).should().editProfileMessage(soptampUser.getUserId(), newProfileMessage);
    }
}