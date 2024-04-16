package org.sopt.app.application.mission;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;

import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.sopt.app.application.mission.MissionInfo.Completeness;
import org.sopt.app.domain.entity.Mission;
import org.sopt.app.domain.entity.Stamp;
import org.sopt.app.interfaces.postgres.MissionRepository;
import org.sopt.app.interfaces.postgres.StampRepository;

@ExtendWith(MockitoExtension.class)
class MissionServiceTest {

    @Mock
    private MissionRepository missionRepository;

    @Mock
    private StampRepository stampRepository;

    @InjectMocks
    private MissionService missionService;

    @Test
    @DisplayName("SUCCESS_모든 미션 조회")
    void SUCCESS_findAllMission() {
        // given
        final Long anyUserId = anyLong();
        final List<Stamp> stampList = List.of(
                Stamp.builder().missionId(1L).build(),
                Stamp.builder().missionId(2L).build()
        );
        final List<Mission> missionList = List.of(
                Mission.builder().id(1L).title("title1").level(1).build(),
                Mission.builder().id(2L).title("title2").level(2).build(),
                Mission.builder().id(3L).title("title3").level(3).build()
        );

        // when
        when(stampRepository.findAllByUserId(anyUserId)).thenReturn(stampList);
        when(missionRepository.findAllByDisplay(true)).thenReturn(missionList);

        List<Completeness> result = missionService.findAllMission(anyUserId);
        List<Completeness> expected = List.of(
                Completeness.builder().id(1L).title("title1").level(1).isCompleted(true).build(),
                Completeness.builder().id(2L).title("title2").level(2).isCompleted(true).build(),
                Completeness.builder().id(3L).title("title3").level(3).isCompleted(false).build()
        );

        // then
        assertThat(result).usingRecursiveComparison().isEqualTo(expected);
    }

    /* TODO: implement following test case

    @Test
    void uploadMission() {
    }

    @Test
    void getCompleteMission() {
    }

    @Test
    void getIncompleteMission() {
    }

    @Test
    void getMissionById() {
    }

    */
}