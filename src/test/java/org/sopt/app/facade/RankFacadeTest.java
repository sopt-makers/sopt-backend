package org.sopt.app.facade;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.BDDMockito.given;
import static org.sopt.app.common.fixtures.SoptampUserFixture.*;

import java.util.Collections;
import java.util.List;
import java.util.stream.Stream;
import org.assertj.core.groups.Tuple;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.sopt.app.application.rank.RankCacheService;
import org.sopt.app.application.soptamp.SoptampPointInfo.Main;
import org.sopt.app.application.soptamp.SoptampPointInfo.PartRank;
import org.sopt.app.application.soptamp.SoptampUserFinder;
import org.sopt.app.domain.enums.Part;

@ExtendWith(MockitoExtension.class)
class RankFacadeTest {

    @Mock
    private SoptampUserFinder soptampUserFinder;

    @Mock
    private RankCacheService rankCacheService;

    @InjectMocks
    private RankFacade rankFacade;

    @Test
    @DisplayName("SUCCESS_동점자가 포함된 현재 기수의 솝탬프 유저 랭킹을 정상적으로 조회함")
    void SUCCESS_findCurrentRanks() {
        //given
        given(rankCacheService.getRanking()).willReturn(Collections.emptySet());
        given(soptampUserFinder.findAllOfCurrentGeneration()).willReturn(SOPTAMP_USER_INFO_LIST);

        // when
        List<Main> result = rankFacade.findCurrentRanks();

        // then
        assertEquals(SOPTAMP_USER_INFO_LIST.size(), result.size());

        assertThat(result)
            .extracting("rank", "nickname", "point")
            .contains(
                Tuple.tuple(1, SOPTAMP_USER_6.getNickname(), SOPTAMP_USER_6.getTotalPoints()),
                Tuple.tuple(2, SOPTAMP_USER_5.getNickname(), SOPTAMP_USER_5.getTotalPoints()),
                Tuple.tuple(5, SOPTAMP_USER_2.getNickname(), SOPTAMP_USER_2.getTotalPoints()),
                Tuple.tuple(6, SOPTAMP_USER_1.getNickname(), SOPTAMP_USER_1.getTotalPoints())
            );

        List<Main> tiedUsers = result.stream()
            .filter(main -> main.getPoint().equals(SOPTAMP_USER_3.getTotalPoints()))
            .toList();

        assertThat(tiedUsers)
            .hasSize(2)
            .extracting("nickname", "point")
            .containsExactlyInAnyOrder(
                Tuple.tuple(SOPTAMP_USER_3.getNickname(), SOPTAMP_USER_3.getTotalPoints()),
                Tuple.tuple(SOPTAMP_USER_4.getNickname(), SOPTAMP_USER_4.getTotalPoints())
            );

        assertThat(tiedUsers)
            .extracting(Main::getRank)
            .containsExactlyInAnyOrder(3, 4);
    }


//    @Test
//    @DisplayName("SUCCESS 파트별 현재 기수의 솝탬프 유저 랭킹 조회")
//    void findCurrentRanksByPart() {
//        // given
//        given(soptampUserFinder.findAllByPartAndCurrentGeneration(Part.SERVER)).willReturn(SERVER_PART_SOPTAMP_USER_INFO_LIST);
//        // when
//        List<Main> result = rankFacade.findCurrentRanksByPart(Part.SERVER);
//        List<Main> expected = List.of(
//                Main.builder().rank(1)
//                        .nickname(SOPTAMP_USER_6.getNickname())
//                        .point(SOPTAMP_USER_6.getTotalPoints()).build(),
//                Main.builder().rank(2)
//                        .nickname(SOPTAMP_USER_5.getNickname())
//                        .point(SOPTAMP_USER_5.getTotalPoints()).build(),
//                Main.builder().rank(3)
//                        .nickname(SOPTAMP_USER_1.getNickname())
//                        .point(SOPTAMP_USER_1.getTotalPoints()).build()
//        );
//        // then
//        assertEquals(expected.size(), result.size());
//        for (int i = 0; i < expected.size(); i++) {
//            assertEquals(expected.get(i).getNickname(), result.get(i).getNickname(), i + "번째 index");
//        }
//    }
//
//    @Test
//    @DisplayName("SUCCESS 파트끼리의 솝탬프 포인트 랭킹 조회")
//    void findAllPartRanks() {
//        // given
//        given(soptampUserFinder.findAllOfCurrentGeneration()).willReturn(SOPTAMP_USER_INFO_LIST);
//        // when
//        List<PartRank> result = rankFacade.findAllPartRanks();
//        List<PartRank> expected = List.of( // 기-디-웹-아-안-서 순서
//                PartRank.builder().rank(5) // 동점이라면 rank도 같아야 함
//                        .part(Part.PLAN.getPartName())
//                        .points(0L).build(),
//                PartRank.builder().rank(2)
//                        .part(Part.DESIGN.getPartName())
//                        .points(SOPTAMP_USER_4.getTotalPoints()).build(),
//                PartRank.builder().rank(5)
//                        .part(Part.WEB.getPartName())
//                        .points(0L).build(),
//                PartRank.builder().rank(2)
//                        .part(Part.IOS.getPartName())
//                        .points(SOPTAMP_USER_3.getTotalPoints()).build(),
//                PartRank.builder().rank(4) // 2등, 2등 다음 rank는 3등이 아닌 4등이다.
//                        .part(Part.ANDROID.getPartName())
//                        .points(SOPTAMP_USER_2.getTotalPoints()).build(),
//                PartRank.builder().rank(1)
//                        .part(Part.SERVER.getPartName())
//                        .points(SOPTAMP_USER_6.getTotalPoints() +
//                                SOPTAMP_USER_5.getTotalPoints() +
//                                SOPTAMP_USER_1.getTotalPoints()).build()
//        );
//
//        // then
//        assertEquals(expected.size(), result.size());
//        for (int i = 0; i < expected.size(); i++) {
//            assertEquals(expected.get(i).getPart(), result.get(i).getPart(), i + "번째 index");
//            assertEquals(expected.get(i).getRank(), result.get(i).getRank(), i + "번째 index");
//            assertEquals(expected.get(i).getPoints(), result.get(i).getPoints(), i + "번째 index");
//        }
//    }
}