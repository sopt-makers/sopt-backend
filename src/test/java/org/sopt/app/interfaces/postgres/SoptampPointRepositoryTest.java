package org.sopt.app.interfaces.postgres;

import java.util.List;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.sopt.app.common.config.QuerydslConfiguration;
import org.sopt.app.domain.entity.SoptampPoint;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;

@DataJpaTest
@AutoConfigureTestDatabase(replace = Replace.NONE)
@Import(QuerydslConfiguration.class)
class SoptampPointRepositoryTest {

    @Autowired
    private SoptampPointRepository soptampPointRepository;

    private SoptampPoint generation1soptampPointId1;
    private SoptampPoint generation1soptampPointId2;
    private SoptampPoint generation2soptampPointId3;

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

        generation2soptampPointId3 = soptampPointRepository.save(
                SoptampPoint.builder()
                        .generation(2L)
                        .soptampUserId(3L)
                        .build()
        );
    }

    @Test
    @DisplayName("SUCCESS_기수별 솝탬프 포인트 찾기")
    void SUCCESS_findAllByGeneration() {
        Assertions.assertThat(soptampPointRepository.findAllByGeneration(1L))
                .hasSameElementsAs(
                        List.of(generation1soptampPointId1, generation1soptampPointId2)
                );
    }

    /* TODO: Implement following test
    @Test
    void SUCCESS_findAllBySoptampUserIdAndGeneration() {
    }

    @Test
    void SUCCESS_findAllBySoptampUserIdInAndGeneration() {
    }
    */
}