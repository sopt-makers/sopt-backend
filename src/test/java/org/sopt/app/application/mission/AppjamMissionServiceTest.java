package org.sopt.app.application.mission;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

import java.util.List;
import org.assertj.core.groups.Tuple;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.sopt.app.application.mission.MissionInfo.AppjamMissionInfo;
import org.sopt.app.common.fixtures.MissionFixture;
import org.sopt.app.common.fixtures.SoptampFixture;
import org.sopt.app.domain.entity.AppjamUser;
import org.sopt.app.domain.entity.soptamp.Mission;
import org.sopt.app.domain.entity.soptamp.SoptampUser;
import org.sopt.app.domain.entity.soptamp.Stamp;
import org.sopt.app.domain.enums.TeamNumber;
import org.sopt.app.interfaces.postgres.AppjamUserRepository;
import org.sopt.app.interfaces.postgres.MissionRepository;
import org.sopt.app.interfaces.postgres.SoptampUserRepository;
import org.sopt.app.interfaces.postgres.StampRepository;

@ExtendWith(MockitoExtension.class)
class AppjamMissionServiceTest {

    @Mock
    private AppjamUserRepository appjamUserRepository;

    @Mock
    private MissionRepository missionRepository;

    @Mock
    private StampRepository stampRepository;

    @Mock
    private SoptampUserRepository soptampUserRepository;

    @InjectMocks
    private AppjamMissionService appjamMissionService;

    @Test
    @DisplayName("SUCCESS_팀 번호가 없을 때 화면에 표시되는 모든 앱잼 미션을 정상적으로 조회함")
    void SUCCESS_getMissions_whenTeamNumberIsNull() {
        // given
        Mission mission1 = MissionFixture.getMissionWithTitleAndLevel("test1", 2);
        Mission mission2 = MissionFixture.getMissionWithTitleAndLevel("test2", 1);
        List<Mission> displayedMissions = List.of(mission2, mission1);

        when(missionRepository.findAllByDisplayOrderByLevelAscTitleAsc(true)).thenReturn(displayedMissions);

        // when
        List<AppjamMissionInfo> result = appjamMissionService.getMissions(null, null);

        // then
        assertThat(result)
            .hasSize(2)
            .extracting(AppjamMissionInfo::getId, AppjamMissionInfo::getTitle)
            .containsExactly(
                Tuple.tuple(mission2.getId(), mission2.getTitle()),
                Tuple.tuple(mission1.getId(), mission1.getTitle())
            );
    }

    @Test
    @DisplayName("SUCCESS_특정 팀 번호로 미션을 조회할 때 앱잼 미션 및 스탬프 정보와 함께 조회함")
    void SUCCESS_getMissions_whenTeamNumberIsProvided() {
        // given
        TeamNumber teamNumber = TeamNumber.FIRST;
        Long userId = 100L;
        
        AppjamUser appjamUser = AppjamUser.builder().userId(userId).teamNumber(teamNumber).build();
        when(appjamUserRepository.findAllByTeamNumber(teamNumber)).thenReturn(List.of(appjamUser));

        Mission mission1 = MissionFixture.getMissionWithTitleAndLevel("test1", 1);
        List<Mission> displayedMissions = List.of(mission1);
        when(missionRepository.findAllByDisplayOrderByLevelAscTitleAsc(true)).thenReturn(displayedMissions);

        Stamp stamp = SoptampFixture.getStampWithUserIdAndMissionId(userId, mission1.getId());
        when(stampRepository.findAllByUserIdIn(List.of(userId))).thenReturn(List.of(stamp));

        SoptampUser soptampUser = SoptampUser.builder().userId(userId).nickname("testNickname").build();
        when(soptampUserRepository.findAllByUserIdIn(List.of(userId))).thenReturn(List.of(soptampUser));

        // when
        List<AppjamMissionInfo> result = appjamMissionService.getMissions(teamNumber, null);

        // then
        assertThat(result)
            .hasSize(1)
            .extracting(AppjamMissionInfo::getId, AppjamMissionInfo::isCompleted, AppjamMissionInfo::getOwnerName)
            .containsExactly(
                Tuple.tuple(mission1.getId(), true, "testNickname")
            );
    }
}
