package org.sopt.app.interfaces.postgres;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.fail;
import static org.sopt.app.common.fixtures.SoptampPointFixture.SOPTAMP_POINT_1;
import static org.sopt.app.common.fixtures.SoptampPointFixture.SOPTAMP_POINT_2;
import static org.sopt.app.common.fixtures.SoptampPointFixture.SOPTAMP_POINT_3;

import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.sopt.app.common.config.QuerydslConfiguration;
import org.sopt.app.domain.entity.SoptampPoint;
import org.sopt.app.interfaces.postgres.soptamp_point.SoptampPointRepository;
import org.sopt.app.interfaces.postgres.soptamp_point.SoptampPointRepositoryImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;

@DataJpaTest
@AutoConfigureTestDatabase(replace = Replace.NONE)
@Import({QuerydslConfiguration.class, SoptampPointRepositoryImpl.class})
class SoptampPointRepositoryTest {

    @Autowired
    private SoptampPointRepository soptampPointRepository;

    @Autowired
    private SoptampPointRepository soptampUserRepository;

    @BeforeEach
    void beforeTest() {
        soptampPointRepository.save(SOPTAMP_POINT_1);
        soptampPointRepository.save(SOPTAMP_POINT_2);
        soptampPointRepository.save(SOPTAMP_POINT_3);
    }

    @Test
    @DisplayName("SUCCESS_기수별 솝탬프 포인트 찾기")
    void SUCCESS_findAllByGeneration() {
        // given & when
        List<SoptampPoint> result = soptampPointRepository.findAllByGeneration(1L);

        // then
        List<Long> resultSoptampPointIdList = result.stream().map(SoptampPoint::getId).toList();

        assertThat(resultSoptampPointIdList)
                .containsAll(List.of(SOPTAMP_POINT_1.getId(), SOPTAMP_POINT_2.getId(), SOPTAMP_POINT_3.getId()));
    }

    @Test
    @DisplayName("SUCCESS_유저 아이디와 기수로 솝탬프 포인트 리스트 찾기")
    void SUCCESS_findAllBySoptampUserIdAndGeneration() {
        // given & when
        SoptampPoint soptampPoint =
                soptampPointRepository.findBySoptampUserIdAndGeneration(
                        SOPTAMP_POINT_1.getSoptampUserId(), SOPTAMP_POINT_1.getGeneration())
                        .orElseThrow();
        // then
        assertThat(soptampPoint.getId()).isEqualTo(SOPTAMP_POINT_1.getId());
    }

    @Test
    @DisplayName("SUCCESS_유저 아이디 리스트와 기수로 솝탬프 포인트 리스트 찾기")
    void SUCCESS_findAllBySoptampUserIdInAndGeneration() {
        // given
        List<Long> userIdList = List.of(SOPTAMP_POINT_1.getSoptampUserId(), SOPTAMP_POINT_2.getSoptampUserId());
        Long generation = 1L;

        // when
        if (!SOPTAMP_POINT_1.getGeneration().equals(generation) ||
                !SOPTAMP_POINT_2.getGeneration().equals(generation)) {
            fail("기수가 같아야 정상적인 테스트를 진행할 수 있습니다.");
        }

        List<SoptampPoint> result =
                soptampPointRepository.findAllBySoptampUserIdInAndGeneration(userIdList, generation);

        // then
        List<Long> resultSoptampPointIdList = result.stream().map(SoptampPoint::getId).toList();

        assertThat(resultSoptampPointIdList).hasSameElementsAs(List.of(
                SOPTAMP_POINT_1.getId(),
                SOPTAMP_POINT_2.getId()
        ));
    }

    @Test
    @DisplayName("SUCCESS_유저 아이디 리스트와 기수로 솝탬프 포인트 리스트 찾기")
    void SUCCESS_findSumOfPointBySamePartAndGeneration() {
        // given
        List<Long> userIdList = List.of(1L, 2L);

        // when
        //Long result = soptampPointRepository.findSumOfPointBySamePartAndGeneration(Part part, Long generation);

        // then

    }
}