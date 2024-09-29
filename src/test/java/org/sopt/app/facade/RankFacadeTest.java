package org.sopt.app.facade;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.BDDMockito.given;
import static org.sopt.app.common.fixtures.SoptampUserFixture.*;

import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.sopt.app.application.soptamp.SoptampPointInfo.Main;
import org.sopt.app.application.soptamp.SoptampUserFinder;
import org.sopt.app.domain.enums.Part;

@ExtendWith(MockitoExtension.class)
class RankFacadeTest {

    @Mock
    private SoptampUserFinder soptampUserFinder;

    @InjectMocks
    private RankFacade rankFacade;

    @Test
    @DisplayName("SUCCESS_현재 기수의 솝탬프 유저 랭킹 조회")
    void SUCCESS_findCurrentRanks() {
        //given
        given(soptampUserFinder.findAllCurrentGenerationSoptampUsers()).willReturn(SOPTAMP_USER_INFO_LIST);

        // when
        List<Main> result = rankFacade.findCurrentRanks();
        List<Main> expected = List.of(
                Main.builder().rank(3)
                        .nickname(SOPTAMP_USER_3.getNickname())
                        .point(SOPTAMP_USER_3.getTotalPoints()).build(),
                Main.builder().rank(3)
                        .nickname(SOPTAMP_USER_4.getNickname())
                        .point(SOPTAMP_USER_4.getTotalPoints()).build(),
                Main.builder().rank(2)
                        .nickname(SOPTAMP_USER_2.getNickname())
                        .point(SOPTAMP_USER_2.getTotalPoints()).build(),
                Main.builder().rank(1)
                        .nickname(SOPTAMP_USER_1.getNickname())
                        .point(SOPTAMP_USER_1.getTotalPoints()).build()
        );

        // then
        assertEquals(expected.size(), result.size());
        for (int i = 0; i < expected.size(); i++) {
            assertEquals(expected.get(i).getNickname(), result.get(i).getNickname());
        }
    }

    @Test
    @DisplayName("SUCCESS 파트별 현재 기수의 솝탬프 유저 랭킹 조회")
    void findCurrentRanksByPart() {
        // given
        given(soptampUserFinder.findSoptampUserIdByPart(Part.SERVER)).willReturn(SERVER_PART_SOPTAMP_USER_INFO_LIST);
        // when
        List<Main> result = rankFacade.findCurrentRanksByPart(Part.SERVER);
        List<Main> expected = List.of(
                Main.builder().rank(1)
                        .nickname(SOPTAMP_USER_6.getNickname())
                        .point(SOPTAMP_USER_6.getTotalPoints()).build(),
                Main.builder().rank(2)
                        .nickname(SOPTAMP_USER_5.getNickname())
                        .point(SOPTAMP_USER_5.getTotalPoints()).build(),
                Main.builder().rank(3)
                        .nickname(SOPTAMP_USER_1.getNickname())
                        .point(SOPTAMP_USER_1.getTotalPoints()).build()
        );
        // then
        assertEquals(expected.size(), result.size());
        for (int i = 0; i < expected.size(); i++) {
            assertEquals(expected.get(i).getNickname(), result.get(i).getNickname());
        }
    }
    /**
    @Test
    @DisplayName("SUCCESS 파트끼리의 솝탬프 포인트 랭킹 조회")
    void findAllPartRanks() {

        List<PartRank> result = rankFacade.findAllPartRanks();
    }
    **/
}