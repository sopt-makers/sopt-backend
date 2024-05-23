package org.sopt.app.interfaces.postgres;

import java.util.List;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.sopt.app.common.config.QuerydslConfiguration;
import org.sopt.app.domain.entity.Mission;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;

@DataJpaTest
@AutoConfigureTestDatabase(replace = Replace.NONE)
@Import(QuerydslConfiguration.class)
class MissionRepositoryTest {

    @Autowired
    private MissionRepository missionRepository;

    private Mission displayedMission1;
    private Mission displayedMission2;
    private Mission notDisplayedMission;

    @BeforeEach
    void beforeTest() {
        displayedMission1 = missionRepository.save(
                Mission.builder()
                        .display(true)
                        .title("title1")
                        .level(1)
                        .build()
        );
        displayedMission2 = missionRepository.save(
                Mission.builder()
                        .display(true)
                        .title("title2")
                        .level(2)
                        .build()
        );
        notDisplayedMission = missionRepository.save(
                Mission.builder()
                        .display(false)
                        .title("title3")
                        .level(3)
                        .build()
        );
    }

    @Test
    @DisplayName("SUCCESS_미션 아이디 리스트로 Display True인 미션 리스트 조회")
    void SUCCESS_findMissionInOrderByLevelAndTitleAndDisplayTrue() {
        // given
        List<Long> missionList = List.of(
                displayedMission1.getId(),
                notDisplayedMission.getId(),
                displayedMission2.getId()
        );

        // when
        List<Mission> result = missionRepository.findMissionInOrderByLevelAndTitleAndDisplayTrue(missionList);
        List<Mission> expected = List.of(displayedMission1, displayedMission2);

        // then
        Assertions.assertThat(result).hasSameElementsAs(expected);
    }

    @Test
    @DisplayName("SUCCESS_미션 아이디 리스트로 미션 리스트 조회")
    void SUCCESS_findMissionInOrderByLevelAndTitle() {
        // given
        List<Long> missionList = List.of(
                displayedMission1.getId(),
                displayedMission2.getId(),
                notDisplayedMission.getId()
        );

        // when
        List<Mission> result = missionRepository.findMissionInOrderByLevelAndTitle(missionList);
        List<Mission> expected = List.of(displayedMission1, displayedMission2, notDisplayedMission);

        // then
        Assertions.assertThat(result).hasSameElementsAs(expected);
    }

    @Test
    @DisplayName("SUCCESS_Display True인 모든 미션 리스트 조회")
    void SUCCESS_findAllByDisplay() {
        List<Mission> result = missionRepository.findAllByDisplay(true);

        Assertions.assertThat(result)
                .containsAll(List.of(displayedMission1, displayedMission2))
                .doesNotContain(notDisplayedMission);
    }
}