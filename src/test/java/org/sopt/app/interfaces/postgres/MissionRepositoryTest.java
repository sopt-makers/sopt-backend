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

    private Mission mission1;
    private Mission mission2;
    private Mission mission3;

    @BeforeEach
    void beforeTest() {
        mission1 = missionRepository.save(
                Mission.builder()
                        .display(true)
                        .build()
        );
        mission2 = missionRepository.save(
                Mission.builder()
                        .display(false)
                        .build()
        );
        mission3 = missionRepository.save(
                Mission.builder()
                        .display(true)
                        .build()
        );
    }

    @Test
    @DisplayName("SUCCESS_미션 아이디 리스트로 Display True인 미션 리스트 조회")
    void SUCCESS_findMissionInOrderByLevelAndTitleAndDisplayTrue() {
        // given
        List<Long> missionList = List.of(
                mission1.getId(),
                mission2.getId(),
                mission3.getId()
        );

        // when
        List<Mission> result = missionRepository.findMissionInOrderByLevelAndTitleAndDisplayTrue(missionList);
        List<Mission> expected = List.of(mission1, mission3);

        // then
        Assertions.assertThat(result).containsExactlyElementsOf(expected);
    }

    @Test
    @DisplayName("SUCCESS_미션 아이디 리스트로 미션 리스트 조회")
    void SUCCESS_findMissionInOrderByLevelAndTitle() {
        // given
        List<Long> missionList = List.of(
                mission1.getId(),
                mission2.getId(),
                mission3.getId()
        );

        // when
        List<Mission> result = missionRepository.findMissionInOrderByLevelAndTitle(missionList);
        List<Mission> expected = List.of(mission1, mission2, mission3);

        // then
        Assertions.assertThat(result).containsExactlyElementsOf(expected);
    }

    /* TODO: implement following test
    @Test
    void findAllByDisplay() {
    }

    */
}