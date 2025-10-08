package org.sopt.app.application;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import jakarta.persistence.criteria.CriteriaBuilder.In;
import java.util.List;
import java.util.Optional;
import org.assertj.core.groups.Tuple;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.sopt.app.application.mission.MissionInfo.Completeness;
import org.sopt.app.application.mission.MissionInfo.Level;
import org.sopt.app.application.mission.MissionService;
import org.sopt.app.application.stamp.StampDeletedEvent;
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

    @Test
    @DisplayName("SUCCESS_새로운 미션을 정상적으로 업로드함")
    void SUCCESS_uploadMission() {
        // given
        final String image = "upload test image";
        final String title = "upload test title";
        final Integer level = 2;

        RegisterMissionRequest registerMissionRequest = new RegisterMissionRequest(image, title, level);

        Mission expected = Mission.builder()
            .title(title)
            .level(level)
            .profileImage(List.of(image))
            .display(true)
            .build();

        when(missionRepository.save(any(Mission.class))).thenReturn(expected);

        // when
        Mission result = missionService.uploadMission(registerMissionRequest);

        // then
        ArgumentCaptor<Mission> missionRepositoryArgumentCaptor = ArgumentCaptor.forClass(Mission.class);
        verify(missionRepository).save(missionRepositoryArgumentCaptor.capture());
        Mission capturedMission = missionRepositoryArgumentCaptor.getValue();

        assertThat(capturedMission).usingRecursiveComparison().isEqualTo(expected);

        assertThat(result).usingRecursiveComparison().isEqualTo(expected);
    }

    @Test
    @DisplayName("SUCCESS_완료한 미션만 level 오름차순, title 오름차순으로 조회함")
    void SUCCESS_getCompleteMission() {
        // given
        final Long userId = 1L;

        final Mission mission1 = MissionFixture.getMissionWithTitleAndLevel("test1", 3);
        final Mission mission2 = MissionFixture.getMissionWithTitleAndLevel("test2", 2);
        final Mission mission3 = MissionFixture.getMissionWithTitleAndLevel("test3", 2);

        List<Mission> expectedSortedMissions = List.of(mission2, mission3, mission1);
        List<Stamp> completedStamps = List.of(
            SoptampFixture.getStampWithUserIdAndMissionId(userId, mission1.getId()),
            SoptampFixture.getStampWithUserIdAndMissionId(userId, mission2.getId()),
            SoptampFixture.getStampWithUserIdAndMissionId(userId, mission3.getId())
        );

        when(stampRepository.findAllByUserId(userId)).thenReturn(completedStamps);
        when(missionRepository.findMissionInOrderByLevelAndTitle(
            List.of(mission1.getId(), mission2.getId(), mission3.getId())))
            .thenReturn(expectedSortedMissions);

        // when
        List<Mission> result = missionService.getCompleteMission(userId);

        // then
        assertThat(result).containsAnyElementsOf(expectedSortedMissions);
    }

    @Test
    @DisplayName("SUCCESS_display true인 미완료 미션만 level 오름차순, title 오름차순으로 조회함")
    void SUCCESS_getIncompleteMission() {
        // given
        final Long userId = 1L;
        final Mission mission1 = MissionFixture.getMissionWithTitleAndLevel("test1", 2);
        final Mission mission2 = MissionFixture.getMissionWithTitleAndLevel("test2", 2);
        final Mission mission3 = MissionFixture.getMissionWithTitleAndLevel("test3", 2);
        final Mission mission4 = MissionFixture.getMissionWithTitleAndLevel("test4", 1);

        List<Mission> displayedMissions = List.of(mission1, mission2, mission3, mission4);
        List<Stamp> completedStamps = List.of(
            SoptampFixture.getStampWithUserIdAndMissionId(userId, mission2.getId()));
        List<Mission> sortedInCompletedMissions = List.of(mission4, mission1, mission3);

        when(missionRepository.findAllByDisplay(true)).thenReturn(displayedMissions);
        when(stampRepository.findAllByUserId(userId)).thenReturn(completedStamps);
        when(missionRepository.findMissionInOrderByLevelAndTitleAndDisplayTrue(
            argThat((List<Long> missionIds) -> {
                assertThat(missionIds).containsAnyElementsOf(sortedInCompletedMissions.stream().map(Mission::getId).toList());
                return true;
            })))
            .thenReturn(sortedInCompletedMissions);

        // when
        List<Mission> result = missionService.getIncompleteMission(userId);

        // then
        assertThat(result).containsAnyElementsOf(displayedMissions);
    }

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