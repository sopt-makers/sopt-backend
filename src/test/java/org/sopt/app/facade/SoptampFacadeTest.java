package org.sopt.app.facade;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.sopt.app.common.fixtures.MissionFixture.MISSION_ID;
import static org.sopt.app.common.fixtures.MissionFixture.MISSION_LEVEL;
import static org.sopt.app.common.fixtures.MissionFixture.getMission;
import static org.sopt.app.common.fixtures.MissionFixture.getRankMission;
import static org.sopt.app.common.fixtures.SoptampFixture.NICKNAME;
import static org.sopt.app.common.fixtures.SoptampFixture.STAMP_ID;
import static org.sopt.app.common.fixtures.SoptampFixture.USER_ID;
import static org.sopt.app.common.fixtures.SoptampFixture.getSoptampUserInfo;
import static org.sopt.app.common.fixtures.SoptampFixture.getStampInfo;
import static org.sopt.app.common.fixtures.SoptampFixture.getStampWithUserId;

import java.util.List;
import org.assertj.core.groups.Tuple;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.sopt.app.application.mission.MissionInfo;
import org.sopt.app.application.mission.MissionInfo.Level;
import org.sopt.app.application.mission.MissionService;
import org.sopt.app.application.soptamp.SoptampUserFinder;
import org.sopt.app.application.soptamp.SoptampUserInfo;
import org.sopt.app.application.soptamp.SoptampUserService;
import org.sopt.app.application.stamp.ClapService;
import org.sopt.app.application.stamp.StampInfo;
import org.sopt.app.application.stamp.StampInfo.StampView;
import org.sopt.app.application.stamp.StampService;
import org.sopt.app.common.fixtures.SoptampFixture;
import org.sopt.app.domain.entity.soptamp.Mission;
import org.sopt.app.domain.entity.soptamp.Stamp;
import org.sopt.app.presentation.rank.RankResponse;
import org.sopt.app.presentation.rank.RankResponse.Detail;
import org.sopt.app.presentation.rank.RankResponse.RankMission;
import org.sopt.app.presentation.rank.RankResponseMapper;
import org.sopt.app.presentation.stamp.StampRequest;
import org.sopt.app.presentation.stamp.StampRequest.EditStampRequest;


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
    @Mock
    private RankResponseMapper rankResponseMapper;
    @Mock
    private ClapService clapService;

    @InjectMocks
    private SoptampFacade soptampFacade;


    @Test
    @DisplayName("SUCCESS_스탬프를 정상적으로 업로드함")
    void SUCCESS_uploadStamp() {
        // given
        final StampInfo.Stamp uploadedStamp = SoptampFixture.getStampInfo();
        final StampRequest.RegisterStampRequest registerStampRequest = SoptampFixture.getRegisterStampRequest();

        when(stampService.uploadStamp(registerStampRequest, USER_ID)).thenReturn(uploadedStamp);
        when(missionService.getMissionLevelById(MISSION_ID)).thenReturn(MissionInfo.Level.of(MISSION_LEVEL));
//        given(stampService.uploadStamp(registerStampRequest, USER_ID)).willReturn(uploadedStamp);
//        given(missionService.getMissionLevelById(MISSION_ID)).willReturn(MissionInfo.Level.of(MISSION_LEVEL));

        // when
        StampInfo.Stamp result = soptampFacade.uploadStamp(USER_ID, registerStampRequest);

        // then
        verify(stampService, times(1)).checkDuplicateStamp(USER_ID, MISSION_ID);
        verify(stampService, times(1)).uploadStamp(registerStampRequest, USER_ID);
        verify(soptampUserService, times(1)).addPointByLevel(USER_ID, MISSION_LEVEL);

//        then(stampService).should(times(1)).checkDuplicateStamp(USER_ID, MISSION_ID);
//        then(stampService).should(times(1)).uploadStamp(registerStampRequest,USER_ID);
//        then(soptampUserService).should(times(1)).addPointByLevel(USER_ID, MISSION_LEVEL);

        assertEquals(uploadedStamp, result);
    }

    @Test
    @DisplayName("SUCCESS_스탬프를 정상적으로 수정함")
    void SUCCESS_editStamp() {
        // given
        EditStampRequest editStampRequest = SoptampFixture.getEditStampRequestWithMissionId(MISSION_ID);
        StampInfo.Stamp stamp = getStampInfo();

        when(stampService.editStampContents(editStampRequest, USER_ID)).thenReturn(stamp);

        // when
        soptampFacade.editStamp(USER_ID, editStampRequest);

        // then
        verify(stampService, times(1)).editStampContents(editStampRequest, USER_ID);
    }

    @Test
    @DisplayName("SUCCESS_스탬프를 정상적으로 삭제하여 total point 가 미션 레벨에 맞게 차감됨")
    void SUCCESS_deleteStamp() {
        // given
        Stamp stamp = getStampWithUserId(USER_ID);
        Level missionLevel = MissionInfo.Level.of(MISSION_LEVEL);

        when(stampService.getStampForDelete(STAMP_ID, USER_ID)).thenReturn(stamp);
        when(missionService.getMissionLevelById(stamp.getMissionId())).thenReturn(missionLevel);

        // when
        soptampFacade.deleteStamp(USER_ID, STAMP_ID);

        // then
        verify(stampService, times(1)).deleteStampById(STAMP_ID);
        verify(soptampUserService, times(1)).subtractPointByLevel(USER_ID, missionLevel.getLevel());
    }

    @Test
    @DisplayName("SUCCESS_유저의 모든 스탬프를 정상적으로 삭제함")
    void SUCCESS_deleteStampAll() {
        // given & when
        soptampFacade.deleteStampAll(USER_ID);

        // then
        verify(stampService, times(1)).deleteAllStamps(USER_ID);
        verify(soptampUserService, times(1)).initPoint(USER_ID);
    }

    @Test
    @DisplayName("SUCCESS_솝탬프 유저의 프로필 메시지를 정상적으로 수정함")
    void SUCCESS_editSoptampUserProfileMessage() {
        // given
        final SoptampUserInfo soptampUser = SoptampFixture.getSoptampUserInfo();
        final String newProfileMessage = "new message";

        // when
        soptampFacade.editSoptampUserProfileMessage(USER_ID, newProfileMessage);

        // then
        verify(soptampUserService, times(1)).editProfileMessage(soptampUser.getUserId(), newProfileMessage);
    }

    @Test
    @DisplayName("SUCCESS_missionId와 nickName으로 스탬프를 정상 조회함")
    void SUCCESS_getStampInfo() {
        // given
        final StampInfo.Stamp stampInfo = SoptampFixture.getStampInfo();
        final SoptampUserInfo soptampUserInfo = getSoptampUserInfo();
        final StampView stampView = StampView.of(stampInfo, 0, true, NICKNAME);

        when(soptampUserFinder.findByNickname(NICKNAME)).thenReturn(soptampUserInfo);
        when(stampService.findStamp(MISSION_ID, USER_ID)).thenReturn(stampInfo);

        // when
        StampView result = soptampFacade.getStampInfo(USER_ID, MISSION_ID, NICKNAME);

        // then
        assertThat(result).usingRecursiveComparison().isEqualTo(stampView);
    }

    @Test
    @DisplayName("SUCCESS_nickName 으로 SoptampUser 정보와 성공한 미션 목록을 정상적으로 가져옴")
    void SUCCESS_findSoptampUserAndCompletedMissionByNickname() {
        // given
        Mission mission1 = getMission();
        Mission mission2 = getMission();

        RankResponse.RankMission rankMission1 = getRankMission(mission1.getId());
        RankResponse.RankMission rankMission2 = getRankMission(mission2.getId());

        final SoptampUserInfo soptampUserInfo = getSoptampUserInfo();
        final List<Mission> completedMissions = List.of(mission1, mission2);

        final RankResponse.Detail expected = new RankResponse.Detail(
            soptampUserInfo.getNickname(),
            soptampUserInfo.getProfileMessage(),
            List.of(rankMission1, rankMission2));

        when(soptampUserFinder.findByNickname(NICKNAME)).thenReturn(soptampUserInfo);
        when(missionService.getCompleteMission(USER_ID)).thenReturn(completedMissions);
        when(rankResponseMapper.of(soptampUserInfo, completedMissions)).thenReturn(expected);

        // when
        Detail result = soptampFacade.findSoptampUserAndCompletedMissionByNickname(NICKNAME);

        // then
        assertThat(result)
            .extracting(Detail::getNickname, Detail::getProfileMessage)
            .contains(soptampUserInfo.getNickname(), soptampUserInfo.getProfileMessage());

        assertThat(result.getUserMissions())
            .hasSize(2)
            .extracting(RankMission::getId, RankMission::getLevel)
            .containsExactlyInAnyOrder(
                Tuple.tuple(rankMission1.getId(), MISSION_LEVEL),
                Tuple.tuple(rankMission2.getId(), MISSION_LEVEL)
            );
    }

}