package org.sopt.app.facade;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.BDDMockito.given;
import static org.sopt.app.common.fixtures.SoptampFixture.MISSION_ID;
import static org.sopt.app.common.fixtures.SoptampFixture.MISSION_LEVEL;
import static org.sopt.app.common.fixtures.SoptampFixture.MULTIPART_FILE_LIST;
import static org.sopt.app.common.fixtures.SoptampFixture.NICKNAME;
import static org.sopt.app.common.fixtures.SoptampFixture.STAMP_IMG_PATHS;
import static org.sopt.app.common.fixtures.SoptampFixture.USER_ID;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.sopt.app.application.mission.MissionInfo;
import org.sopt.app.application.mission.MissionService;
import org.sopt.app.application.s3.S3Service;
import org.sopt.app.application.soptamp.SoptampPointService;
import org.sopt.app.application.soptamp.SoptampUserService;
import org.sopt.app.application.stamp.StampInfo;
import org.sopt.app.application.stamp.StampService;
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
    private SoptampPointService soptampPointService;

    @InjectMocks
    private SoptampFacade soptampFacade;


    @Test
    @DisplayName("SUCCESS_스탬프 조회하기")
    void SUCCESS_getStampInfo() {
        // given
        final StampInfo.Stamp stampInfo = SoptampFixture.getStampInfo();
        given(soptampUserService.findByNickname(NICKNAME)).willReturn(SoptampFixture.getUserInfo());
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
        given(stampService.uploadStamp(registerStampRequest, USER_ID)).willReturn(uploadedStamp);
        given(missionService.getMissionById(MISSION_ID)).willReturn(MissionInfo.Level.of(MISSION_LEVEL));
        given(soptampUserService.addPoint(USER_ID, MISSION_LEVEL)).willReturn(SoptampFixture.getUserInfo());

        // when
        StampInfo.Stamp result = soptampFacade.uploadStamp(USER_ID, registerStampRequest);

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
        given(stampService.uploadStampDeprecated(registerStampRequest, STAMP_IMG_PATHS, USER_ID, MISSION_ID)).willReturn(uploadedStamp);
        given(missionService.getMissionById(MISSION_ID)).willReturn(MissionInfo.Level.of(MISSION_LEVEL));
        given(soptampUserService.addPoint(USER_ID, MISSION_LEVEL)).willReturn(SoptampFixture.getUserInfo());

        // when
        StampInfo.Stamp result = soptampFacade.uploadStampDeprecated(USER_ID, MISSION_ID, registerStampRequest, MULTIPART_FILE_LIST);

        // then
        assertEquals(uploadedStamp, result);
    }

    @Test
    @DisplayName("SUCCESS_스탬프 수정하기")
    void SUCCESS_editStamp() {
        // given
        final StampInfo.Stamp editedStamp = SoptampFixture.getStampInfo();
        final EditStampRequest editStampRequest = SoptampFixture.getEditStampRequest();
        given(stampService.editStampContentsDeprecated(editStampRequest, USER_ID, MISSION_ID)).willReturn(editedStamp);
        given(s3Service.uploadDeprecated(MULTIPART_FILE_LIST)).willReturn(SoptampFixture.STAMP_IMG_PATHS);

        // when
        StampInfo.Stamp result = soptampFacade.editStamp(editStampRequest, USER_ID, MISSION_ID, MULTIPART_FILE_LIST);

        // then
        assertEquals(editedStamp, result);
    }

    /* TODO: 아래 메서드 구현
    @Test
    void deleteStamp() {
    }

    @Test
    void deleteStampAll() {
    }

    @Test
    void editSoptampUserNickname() {
    }

    @Test
    void editSoptampUserProfileMessage() {
    }
    */
}