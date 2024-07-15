package org.sopt.app.interfaces.postgres;

import java.util.List;
import org.assertj.core.api.Assertions;
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

    private SoptampPoint generation1soptampPointId1;
    private SoptampPoint generation1soptampPointId2;

    @BeforeEach
    void beforeTest() {
        generation1soptampPointId1 = soptampPointRepository.save(
                SoptampPoint.builder()
                        .generation(1L)
                        .soptampUserId(1L)
                        .build()
        );

        generation1soptampPointId2 = soptampPointRepository.save(
                SoptampPoint.builder()
                        .generation(1L)
                        .soptampUserId(2L)
                        .build()
        );

        soptampPointRepository.save(
                SoptampPoint.builder()
                        .generation(1L)
                        .soptampUserId(3L)
                        .build()
        );

        soptampPointRepository.save(
                SoptampPoint.builder()
                        .generation(2L)
                        .soptampUserId(4L)
                        .build()
        );

    }

    @Test
    @DisplayName("SUCCESS_기수별 솝탬프 포인트 찾기")
    void SUCCESS_findAllByGeneration() {
        Assertions.assertThat(soptampPointRepository.findAllByGeneration(1L))
                .containsAll(
                        List.of(generation1soptampPointId1, generation1soptampPointId2)
                );
    }

    @Test
    @DisplayName("SUCCESS_유저 아이디와 기수로 솝탬프 포인트 리스트 찾기")
    void SUCCESS_findAllBySoptampUserIdAndGeneration() {
        Assertions.assertThat(soptampPointRepository.findBySoptampUserIdAndGeneration(1L, 1L)
                        .orElseThrow().getId())
                .isEqualTo(generation1soptampPointId1.getId());
    }

    @Test
    @DisplayName("SUCCESS_유저 아이디 리스트와 기수로 솝탬프 포인트 리스트 찾기")
    void SUCCESS_findAllBySoptampUserIdInAndGeneration() {
        // given
        List<Long> userIdList = List.of(1L, 2L);

        // when
        List<SoptampPoint> result = soptampPointRepository.findAllBySoptampUserIdInAndGeneration(userIdList, 1L);

        // then
        Assertions.assertThat(result)
                .hasSameElementsAs(
                        List.of(generation1soptampPointId1, generation1soptampPointId2)
                );
    }
}