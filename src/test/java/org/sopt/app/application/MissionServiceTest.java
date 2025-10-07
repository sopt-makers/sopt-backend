package org.sopt.app.application;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Optional;
import org.assertj.core.groups.Tuple;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.sopt.app.application.mission.MissionInfo.Completeness;
import org.sopt.app.application.mission.MissionInfo.Level;
import org.sopt.app.application.mission.MissionService;
import org.sopt.app.common.fixtures.MissionFixture;
import org.sopt.app.common.fixtures.SoptampFixture;
import org.sopt.app.domain.entity.soptamp.Mission;
import org.sopt.app.domain.entity.soptamp.Stamp;
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

    private final List<Stamp> stampList = List.of(
            Stamp.builder().missionId(1L).build(),
            Stamp.builder().missionId(2L).build()
    );

    private final List<Mission> displayedMissionList = List.of(
            Mission.builder().id(1L).title("title1").level(1).display(true).build(),
            Mission.builder().id(2L).title("title2").level(2).display(true).build(),
            Mission.builder().id(3L).title("title3").level(3).display(true).build()
    );

    @Test
    @DisplayName("SUCCESS_유저의 성공 여부를 포함한 모든 미션 목록을 정상적으로 조회함")
    void SUCCESS_findAllMission() {
        // given
        final Long userId = 1L;
        final Mission mission1 = MissionFixture.getMission();
        final Mission mission2 = MissionFixture.getMission();
        final Mission mission3 = MissionFixture.getMission();

        List<Mission> displayedMissions = List.of(mission1, mission2, mission3);
        List<Stamp> completedStamps = List.of(
            SoptampFixture.getStampWithUserIdAndMissionId(userId, mission1.getId()),
            SoptampFixture.getStampWithUserIdAndMissionId(userId, mission2.getId())
        );

        when(stampRepository.findAllByUserId(userId)).thenReturn(completedStamps);
        when(missionRepository.findAllByDisplay(true)).thenReturn(displayedMissions);

        // when
        List<Completeness> result = missionService.findAllMission(userId);

        // then
        assertThat(result)
            .hasSize(3)
            .extracting(Completeness::getId, Completeness::getTitle, Completeness::getIsCompleted)
            .containsExactlyInAnyOrder(
                Tuple.tuple(mission1.getId(), mission1.getTitle(), isCompletedMission(completedStamps, mission1)),
                Tuple.tuple(mission2.getId(), mission2.getTitle(), isCompletedMission(completedStamps, mission2)),
                Tuple.tuple(mission3.getId(), mission3.getTitle(), isCompletedMission(completedStamps, mission3))
            );
    }

//    @Test
//    @DisplayName("SUCCESS_미션 업로드")
//    void SUCCESS_uploadMission() {
//        // given
//        RegisterMissionRequest registerMissionRequest = new RegisterMissionRequest("image", "title", 1);
//
//        // when
//        Mission expected = Mission.builder()
//                .title("title")
//                .level(1)
//                .profileImage(List.of("image"))
//                .build();
//        when(missionRepository.save(any(Mission.class))).thenReturn(expected);
//
//        Mission result = missionService.uploadMission(registerMissionRequest);
//
//        // then
//        assertThat(result).usingRecursiveComparison().isEqualTo(expected);
//    }
//
//    @Test
//    @DisplayName("SUCCESS_완료한 미션 조회")
//    void SUCCESS_getCompleteMission() {
//        // given
//        final Long anyUserId = anyLong();
//        final List<Long> missionIdList = List.of(1L, 2L);
//
//        // when
//        final List<Mission> expected = List.of(
//                Mission.builder().id(1L).build(),
//                Mission.builder().id(2L).build()
//        );
//        when(stampRepository.findAllByUserId(anyUserId)).thenReturn(stampList);
//        when(missionRepository.findMissionInOrderByLevelAndTitle(missionIdList)).thenReturn(expected);
//
//        List<Mission> result = missionService.getCompleteMission(anyUserId);
//
//        // then
//        assertThat(result).usingRecursiveComparison().isEqualTo(expected);
//    }
//
//    @Test
//    @DisplayName("SUCCESS_미완료한 미션 조회")
//    void SUCCESS_getIncompleteMission() {
//        // given
//        Long anyUserId = anyLong();
//
//        // when
//        List<Mission> expected = List.of(Mission.builder().id(3L).build());
//
//        when(stampRepository.findAllByUserId(anyUserId)).thenReturn(stampList);
//        when(missionRepository.findMissionInOrderByLevelAndTitleAndDisplayTrue(any())).thenReturn(expected);
//        when(missionRepository.findAllByDisplay(true)).thenReturn(displayedMissionList);
//
//        List<Mission> result = missionService.getIncompleteMission(anyUserId);
//
//        // then
//        assertThat(result).usingRecursiveComparison().isEqualTo(expected);
//    }
//
//    @Test
//    @DisplayName("SUCCESS_미션 조회")
//    void SUCCESS_getMissionById() {
//        // given
//        final Long anyMissionId = anyLong();
//        final Integer level = 1;
//
//        // when
//        when(missionRepository.findById(anyMissionId)).thenReturn(
//                Optional.of(Mission.builder().id(anyMissionId).level(level).build()));
//        Level result = missionService.getMissionById(anyMissionId);
//
//        // then
//        assertThat(result.getLevel()).isEqualTo(level);
//    }
//
//    @Test
//    @DisplayName("FAIL_미션 조회")
//    void FAIL_getMissionById() {
//        // given
//        final Long anyMissionId = anyLong();
//
//        // when
//        when(missionRepository.findById(anyMissionId)).thenReturn(Optional.empty());
//
//        // then
//        Assertions.assertThrows(IllegalArgumentException.class, () -> {
//            missionService.getMissionById(anyMissionId);
//        });
//    }

    private boolean isCompletedMission(List<Stamp> completedStamps, Mission mission){
        return completedStamps.stream()
            .anyMatch(stamp -> stamp.getMissionId().equals(mission.getId()));
    }
}