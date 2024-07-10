package org.sopt.app.application;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.sopt.app.application.soptamp.SoptampPointInfo.PartRank;
import org.sopt.app.application.soptamp.SoptampPointInfo.Point;
import org.sopt.app.application.soptamp.SoptampPointService;
import org.sopt.app.domain.entity.SoptampPoint;
import org.sopt.app.domain.enums.Part;
import org.sopt.app.domain.enums.UserStatus;
import org.sopt.app.interfaces.postgres.soptamp_point.SoptampPointRepository;
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
        when(soptampPointRepository.findAllBySoptampUserIdInAndGeneration(soptampUserIdList, anyGeneration)).thenReturn(
                soptampPointList);
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

    @Test
    @DisplayName("SUCCESS_솝탬프 포인트가 있을 때 포인트 추가")
    void SUCCESS_addPointSoptampIsPresent() {
        //given
        SoptampPoint soptampPoint = SoptampPoint.builder()
                .id(1L)
                .generation(34L)
                .soptampUserId(1L)
                .points(100L)
                .build();

        //when
        when(soptampPointRepository.findBySoptampUserIdAndGeneration(anyLong(), anyLong())).thenReturn(
                Optional.of(soptampPoint));

        //then
        Assertions.assertDoesNotThrow(() -> soptampPointService.addPoint(anyLong(), anyInt()));
    }

    @Test
    @DisplayName("SUCCESS_솝탬프 포인트가 없으면 아무것도 안함")
    void SUCCESS_addPointSoptampNotPresent() {
        //when
        when(soptampPointRepository.findBySoptampUserIdAndGeneration(anyLong(), anyLong())).thenReturn(
                Optional.empty());
        //then
        Assertions.assertDoesNotThrow(() -> soptampPointService.addPoint(anyLong(), anyInt()));
    }

    @Test
    @DisplayName("SUCCESS_솝탬프 포인트가 있을 때 포인트 추가")
    void SUCCESS_subtractPointIsPresent() {
        //given
        SoptampPoint soptampPoint = SoptampPoint.builder()
                .id(1L)
                .generation(34L)
                .soptampUserId(1L)
                .points(100L)
                .build();

        //when
        when(soptampPointRepository.findBySoptampUserIdAndGeneration(anyLong(), anyLong())).thenReturn(
                Optional.of(soptampPoint));

        //then
        Assertions.assertDoesNotThrow(() -> soptampPointService.subtractPoint(anyLong(), anyInt()));
    }

    @Test
    @DisplayName("SUCCESS_솝탬프 포인트가 없으면 아무것도 안함")
    void SUCCESS_subtractPointSoptampNotPresent() {
        //when
        when(soptampPointRepository.findBySoptampUserIdAndGeneration(anyLong(), anyLong())).thenReturn(
                Optional.empty());
        //then
        Assertions.assertDoesNotThrow(() -> soptampPointService.subtractPoint(anyLong(), anyInt()));
    }

    @Test
    @DisplayName("SUCCESS_유저 상태가 ACTIVE일 때 솝탬프 포인트 업서트")
    void SUCCESS_upsertSoptampPointUserStatusACTIVE() {
        //given
        Long anyUserId = anyLong();

        //when
        when(soptampPointRepository.findBySoptampUserIdAndGeneration(anyUserId, anyLong()))
                .thenReturn(Optional.of(new SoptampPoint()));

        //then
        Assertions.assertDoesNotThrow(() -> soptampPointService.upsertSoptampPoint(UserStatus.ACTIVE, anyUserId));
    }

    @Test
    @DisplayName("SUCCESS_유저 상태가 INACTIVE일 때 업서트하지 않음")
    void SUCCESS_upsertSoptampPointUserStatusINACTIVE() {
        //then
        Assertions.assertDoesNotThrow(() -> soptampPointService.upsertSoptampPoint(UserStatus.INACTIVE, 1L));
    }

    @Test
    @DisplayName("SUCCESS_솝탬프 포인트가 Empty일 때 업서트하지 않음")
    void SUCCESS_upsertSoptampPointNotPresent() {
        //when
        when(soptampPointRepository.findBySoptampUserIdAndGeneration(anyLong(), anyLong()))
                .thenReturn(Optional.empty());
        //then
        Assertions.assertDoesNotThrow(() -> soptampPointService.upsertSoptampPoint(UserStatus.ACTIVE, 1L));
    }

    @Test
    @DisplayName("SUCCESS_파트 랭킹 찾기")
    void SUCCESS_findPartRanks() {
        //given
        Map<Part, Long> partPoints = Map.of(
                Part.PLAN, 50L,
                Part.DESIGN, 0L,
                Part.IOS, 20L,
                Part.ANDROID, 10L,
                Part.WEB, 30L,
                Part.SERVER, 40L
        );

        //when
        Map<Part, PartRank> result = soptampPointService.findPartRanks(partPoints);
        Map<Part, PartRank> expected = Map.of(
                Part.PLAN, PartRank.builder()
                        .part(Part.PLAN.getPartName())
                        .rank(1)
                        .points(50L)
                        .build(),
                Part.DESIGN, PartRank.builder()
                        .part(Part.DESIGN.getPartName())
                        .rank(6)
                        .points(0L)
                        .build(),
                Part.IOS, PartRank.builder()
                        .part(Part.IOS.getPartName())
                        .rank(4)
                        .points(20L)
                        .build(),
                Part.ANDROID, PartRank.builder()
                        .part(Part.ANDROID.getPartName())
                        .rank(5)
                        .points(10L)
                        .build(),
                Part.WEB, PartRank.builder()
                        .part(Part.WEB.getPartName())
                        .rank(3)
                        .points(30L)
                        .build(),
                Part.SERVER, PartRank.builder()
                        .part(Part.SERVER.getPartName())
                        .rank(2)
                        .points(40L)
                        .build()
        );

        //then
        assertThat(result).usingRecursiveComparison().isEqualTo(expected);
    }

    @Test
    @DisplayName("SUCCESS_파트 랭킹 계산")
    void SUCCESS_calculateSumOfPoints() {
        //given
        List<Point> soptampPointList = List.of(
                Point.builder()
                        .points(100L)
                        .build(),
                Point.builder()
                        .points(200L)
                        .build()
        );

        //when
        Long result = soptampPointService.calculateSumOfPoints(soptampPointList);

        //then
        assertThat(result).isEqualTo(300L);
    }
}