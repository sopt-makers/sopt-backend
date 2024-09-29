package org.sopt.app.facade;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.sopt.app.common.fixtures.SoptampPointFixture.PART_POINTS;
import static org.sopt.app.common.fixtures.SoptampPointFixture.PART_RANK_ANDROID;
import static org.sopt.app.common.fixtures.SoptampPointFixture.PART_RANK_DESIGN;
import static org.sopt.app.common.fixtures.SoptampPointFixture.PART_RANK_IOS;
import static org.sopt.app.common.fixtures.SoptampPointFixture.PART_RANK_PLAN;
import static org.sopt.app.common.fixtures.SoptampPointFixture.PART_RANK_SERVER;
import static org.sopt.app.common.fixtures.SoptampPointFixture.PART_RANK_WEB;
import static org.sopt.app.common.fixtures.SoptampPointFixture.POINT_1;
import static org.sopt.app.common.fixtures.SoptampPointFixture.POINT_2;
import static org.sopt.app.common.fixtures.SoptampPointFixture.POINT_3;
import static org.sopt.app.common.fixtures.SoptampPointFixture.SOPTAMP_POINT_LIST;
import static org.sopt.app.common.fixtures.SoptampUserFixture.SOPTAMP_USER_ID_LIST;
import static org.sopt.app.common.fixtures.SoptampUserFixture.SOPTAMP_USER_INFO_1;
import static org.sopt.app.common.fixtures.SoptampUserFixture.SOPTAMP_USER_INFO_2;
import static org.sopt.app.common.fixtures.SoptampUserFixture.SOPTAMP_USER_INFO_3;
import static org.sopt.app.common.fixtures.SoptampUserFixture.SOPTAMP_USER_INFO_LIST;

import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.sopt.app.application.soptamp.SoptampPointInfo.Main;
import org.sopt.app.application.soptamp.SoptampPointInfo.PartRank;
import org.sopt.app.application.soptamp.SoptampUserService;

@ExtendWith(MockitoExtension.class)
class RankFacadeTest {

    @Mock
    private SoptampUserService soptampUserService;

    @Mock
    private SoptampPointService soptampPointService;

    @InjectMocks
    private RankFacade rankFacade;

    @Test
    @DisplayName("SUCCESS_솝탬프 포인트 리스트를 받아 랭크를 조회")
    void SUCCESS_findCurrentRanks() {
        //given
        given(soptampPointService.findCurrentGenerationPoints()).willReturn(SOPTAMP_POINT_LIST);
        given(soptampUserService.findAllBySoptampUserIds(SOPTAMP_USER_ID_LIST)).willReturn(SOPTAMP_USER_INFO_LIST);

        //when
        List<Main> expected = List.of(
                Main.builder().rank(1).nickname(SOPTAMP_USER_INFO_3.getNickname()).point(POINT_3.getPoints()).build(),
                Main.builder().rank(2).nickname(SOPTAMP_USER_INFO_2.getNickname()).point(POINT_2.getPoints()).build(),
                Main.builder().rank(3).nickname(SOPTAMP_USER_INFO_1.getNickname()).point(POINT_1.getPoints()).build()
        );
        List<Main> result = rankFacade.findCurrentRanks();

        //then
        assertThat(result).usingRecursiveComparison().isEqualTo(expected);
    }

    @Test
    @DisplayName("SUCCESS_솝탬프 포인트 해당하는 유저가 없다면 슬랙 알림을 보내기")
    void SUCCESS_findCurrentRanks_Requirement1() {
        //given
        given(soptampPointService.findCurrentGenerationPoints()).willReturn(SOPTAMP_POINT_LIST);
        given(soptampUserService.findAllBySoptampUserIds(SOPTAMP_USER_ID_LIST)).willReturn(
                List.of(SOPTAMP_USER_INFO_1, SOPTAMP_USER_INFO_2) // 3번 유저가 존재하지 않음
        );

        //when
        List<Main> expected = List.of(
                Main.builder().rank(1).nickname(SOPTAMP_USER_INFO_2.getNickname()).point(POINT_2.getPoints()).build(),
                Main.builder().rank(2).nickname(SOPTAMP_USER_INFO_1.getNickname()).point(POINT_1.getPoints()).build()
        );
        List<Main> result = rankFacade.findCurrentRanks();

        //then
        assertThat(result).usingRecursiveComparison().isEqualTo(expected);
    }

    @Test
    @DisplayName("SUCCESS_파트 랭킹 찾기")
    void SUCCESS_findAllPartRanks() {
        //given
        given(soptampPointService.findSumOfPointAllParts()).willReturn(PART_POINTS);

        //when
        List<PartRank> result = rankFacade.findAllPartRanks();
        List<PartRank> expected = List.of(
                PART_RANK_PLAN,
                PART_RANK_DESIGN,
                PART_RANK_WEB,
                PART_RANK_IOS,
                PART_RANK_ANDROID,
                PART_RANK_SERVER
        );

        //then
        assertThat(result).usingRecursiveComparison().isEqualTo(expected);
    }
}
