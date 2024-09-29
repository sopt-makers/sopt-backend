package org.sopt.app.facade;

import static org.junit.jupiter.api.Assertions.assertEquals;
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
import org.sopt.app.application.s3.S3Service;
import org.sopt.app.application.soptamp.*;
import org.sopt.app.application.stamp.*;
import org.sopt.app.common.fixtures.SoptampFixture;
import org.sopt.app.presentation.stamp.StampRequest;
import org.sopt.app.presentation.stamp.StampRequest.EditStampRequest;

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
    @DisplayName("SUCCESS_스탬프 업로드하기(이전 버전)")
    void uploadStampDeprecated() {
        // given
        final StampInfo.Stamp uploadedStamp = SoptampFixture.getStampInfo();
        final StampRequest.RegisterStampRequest registerStampRequest = SoptampFixture.getRegisterStampRequest();
        given(s3Service.uploadDeprecated(MULTIPART_FILE_LIST)).willReturn(SoptampFixture.STAMP_IMG_PATHS);
        given(stampService.uploadStampDeprecated(registerStampRequest, STAMP_IMG_PATHS, SOPTAMP_USER_ID, MISSION_ID))
                .willReturn(uploadedStamp);
        given(missionService.getMissionById(MISSION_ID)).willReturn(MissionInfo.Level.of(MISSION_LEVEL));

        // when
        StampInfo.Stamp result = soptampFacade.uploadStampDeprecated(
                SOPTAMP_USER_ID, MISSION_ID, registerStampRequest, MULTIPART_FILE_LIST);

        // then
        assertEquals(uploadedStamp, result);
    }

    @Test
    @DisplayName("SUCCESS_스탬프 수정하기")
    void SUCCESS_editStamp() {
        // given
        final StampInfo.Stamp editedStamp = SoptampFixture.getStampInfo();
        final EditStampRequest editStampRequest = SoptampFixture.getEditStampRequest();
        given(stampService.editStampContentsDeprecated(editStampRequest, SOPTAMP_USER_ID, MISSION_ID))
                .willReturn(editedStamp);
        given(s3Service.uploadDeprecated(MULTIPART_FILE_LIST)).willReturn(SoptampFixture.STAMP_IMG_PATHS);

        // when
        StampInfo.Stamp result =
                soptampFacade.editStamp(editStampRequest, SOPTAMP_USER_ID, MISSION_ID, MULTIPART_FILE_LIST);

        // then
        assertEquals(editedStamp, result);
    }

    @Test
    @DisplayName("SUCCESS_스탬프 삭제하기")
    void SUCCESS_deleteStamp() {
        // given
        given(stampService.getMissionIdByStampId(STAMP_ID)).willReturn(MISSION_ID);
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