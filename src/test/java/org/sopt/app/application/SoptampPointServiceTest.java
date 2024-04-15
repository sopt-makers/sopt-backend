package org.sopt.app.application;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;

import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.junit.jupiter.api.Test;
import org.mockito.junit.jupiter.MockitoExtension;
import org.sopt.app.application.soptamp.SoptampPointInfo.Point;
import org.sopt.app.application.soptamp.SoptampPointService;
import org.sopt.app.domain.entity.SoptampPoint;
import org.sopt.app.interfaces.postgres.SoptampPointRepository;
import org.springframework.test.util.ReflectionTestUtils;

@ExtendWith(MockitoExtension.class)
class SoptampPointServiceTest {

    @Mock
    private SoptampPointRepository soptampPointRepository;

    @InjectMocks
    private SoptampPointService soptampPointService;

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(soptampPointService, "currentGeneration", 34L);
    }

    @Test
    @DisplayName("SUCCESS_현재 포인트 리스트 찾기")
    void SUCCESS_findCurrentPointList() {
        //given
        final Long anyGeneration = anyLong();

        List<SoptampPoint> soptampPointList = List.of(
            SoptampPoint.builder()
                .id(1L)
                .generation(anyGeneration)
                .soptampUserId(1L)
                .points(100L)
                .build(),
            SoptampPoint.builder()
                .id(2L)
                .generation(anyGeneration)
                .soptampUserId(2L)
                .points(200L)
                .build()
        );

        //when
        when(soptampPointRepository.findAllByGeneration(anyGeneration)).thenReturn(soptampPointList);

        List<Point> result = soptampPointService.findCurrentPointList();
        List<Point> expected = List.of(
                Point.builder()
                        .id(1L)
                        .generation(anyGeneration)
                        .soptampUserId(1L)
                        .points(100L)
                        .build(),
                Point.builder()
                        .id(2L)
                        .generation(anyGeneration)
                        .soptampUserId(2L)
                        .points(200L)
                        .build()

                );

        //then
        assertThat(result).usingRecursiveComparison().isEqualTo(expected);
    }


    @Test
    @DisplayName("SUCCESS_유저 아이디 리스트로 현재 포인트 리스트 찾기")
    void SUCCESS_findCurrentPointListBySoptampUserIds() {
        //given
        List<Long> soptampUserIdList = any();
        Long anyGeneration = anyLong();
        List<SoptampPoint> soptampPointList = List.of(
            SoptampPoint.builder()
                .id(1L)
                .generation(anyGeneration)
                .soptampUserId(1L)
                .points(100L)
                .build(),
            SoptampPoint.builder()
                .id(2L)
                .generation(anyGeneration)
                .soptampUserId(2L)
                .points(200L)
                .build()
        );

        //when
        when(soptampPointRepository.findAllBySoptampUserIdInAndGeneration(soptampUserIdList, anyGeneration)).thenReturn(soptampPointList);
        List<Point> result = soptampPointService.findCurrentPointListBySoptampUserIds(soptampUserIdList);
        List<Point> expected = List.of(
                Point.builder()
                        .id(1L)
                        .generation(anyGeneration)
                        .soptampUserId(1L)
                        .points(100L)
                        .build(),
                Point.builder()
                        .id(2L)
                        .generation(anyGeneration)
                        .soptampUserId(2L)
                        .points(200L)
                        .build()
                );

        //then
        assertThat(result).usingRecursiveComparison().isEqualTo(expected);
    }

    /* TODO: Implement test cases for the following methods
    @Test
    void addPoint() {
    }

    @Test
    void subtractPoint() {
    }

    @Test
    void upsertSoptampPoint() {
    }

    @Test
    void findPartRanks() {
    }

    @Test
    void calculateSumOfPoints() {
    }

     */
}