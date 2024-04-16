package org.sopt.app.application.mission;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;

import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatchers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.sopt.app.application.mission.MissionInfo.Completeness;
import org.sopt.app.domain.entity.Mission;
import org.sopt.app.domain.entity.Stamp;
import org.sopt.app.interfaces.postgres.MissionRepository;
import org.sopt.app.interfaces.postgres.StampRepository;
import org.sopt.app.presentation.mission.MissionRequest.RegisterMissionRequest;

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


    @Test
    @DisplayName("SUCCESS_미션 업로드")
    void SUCCESS_uploadMission() {
        // given
        RegisterMissionRequest registerMissionRequest = new RegisterMissionRequest();
        registerMissionRequest.setTitle("title");
        registerMissionRequest.setLevel(1);
        registerMissionRequest.setImage("image");

        // when
        Mission expected = Mission.builder()
                .title("title")
                .level(1)
                .profileImage(List.of("image"))
                .build();
        when(missionRepository.save(any(Mission.class))).thenReturn(expected);

        Mission result = missionService.uploadMission(registerMissionRequest);

        // then
        assertThat(result).usingRecursiveComparison().isEqualTo(expected);
    }

    /* TODO: implement following test case
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