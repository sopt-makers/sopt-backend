package org.sopt.app.facade;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.given;
import static org.sopt.app.common.fixtures.SoptampUserFixture.*;
import static org.sopt.app.domain.enums.Part.ANDROID;
import static org.sopt.app.domain.enums.Part.DESIGN;
import static org.sopt.app.domain.enums.Part.IOS;
import static org.sopt.app.domain.enums.Part.PLAN;

import java.util.Collections;
import java.util.List;
import org.assertj.core.groups.Tuple;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.sopt.app.application.rank.RankCacheService;
import org.sopt.app.application.soptamp.SoptampPointInfo.Main;
import org.sopt.app.application.soptamp.SoptampPointInfo.PartRank;
import org.sopt.app.application.soptamp.SoptampUserFinder;
import org.sopt.app.common.fixtures.SoptampUserFixture;
import org.sopt.app.domain.entity.soptamp.SoptampUser;
import org.sopt.app.domain.enums.Part;

@ExtendWith(MockitoExtension.class)
class RankFacadeTest {

    @Mock
    private SoptampUserFinder soptampUserFinder;

    @Mock
    private RankCacheService rankCacheService;

    @InjectMocks
    private RankFacade rankFacade;

    @Nested
    @DisplayName("현재 기수의 솝탬프 유저 랭킹 조회 테스트")
    class FindCurrentRanksTest{

        @Nested
        @DisplayName("캐시가 존재하는 경우")
        class FindCurrentRanksWithCache{
            private List<Main> result;

            @BeforeEach
            void setUp(){
                // given
                given(rankCacheService.getRanking()).willReturn(SOPTAMP_SCORE_CACHE);
                given(rankCacheService.getUserInfo(anyLong()))
                    .willAnswer(invocation -> {
                        Long userId  = invocation.getArgument(0);
                        return SOPTAMP_PROFILE_MESSAGE_CACHE.get(userId);
                    });

                // when
                result = rankFacade.findCurrentRanks();
            }

            @Test
            @DisplayName("SUCCESS_현재 기수의 솝탬프 유저 랭킹을 정상적으로 조회함")
            void SUCCESS_findCurrentRanks() {
                assertThat(result)
                    .hasSize(SOPTAMP_USER_INFO_LIST.size())
                    .extracting("rank", "nickname", "point")
                    .contains(
                        Tuple.tuple(1, SOPTAMP_USER_6.getNickname(), SOPTAMP_USER_6.getTotalPoints()),
                        Tuple.tuple(2, SOPTAMP_USER_5.getNickname(), SOPTAMP_USER_5.getTotalPoints()),
                        Tuple.tuple(5, SOPTAMP_USER_2.getNickname(), SOPTAMP_USER_2.getTotalPoints()),
                        Tuple.tuple(6, SOPTAMP_USER_1.getNickname(), SOPTAMP_USER_1.getTotalPoints())
                    );
            }

            @Test
            @DisplayName("SUCCESS_동점자가 존재할 경우 순위 보장은 되지 않으며 동일 순위없이 조회됨")
            void SUCCESS_findCurrentRanks_whenTiedUsers() {
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

        }

        @Nested
        @DisplayName("캐시가 존재하지 않는 경우")
        class FindCurrentRanksWithOutCache{

            private List<Main> result;

            @BeforeEach
            void setUp(){
                // given
                given(rankCacheService.getRanking()).willReturn(Collections.emptySet());
                given(soptampUserFinder.findAllOfCurrentGeneration()).willReturn(SOPTAMP_USER_INFO_LIST);

                // when
                result = rankFacade.findCurrentRanks();
            }

            @Test
            @DisplayName("SUCCESS_현재 기수의 솝탬프 유저 랭킹을 정상적으로 조회함")
            void SUCCESS_findCurrentRanks() {
                assertThat(result)
                    .hasSize(SOPTAMP_USER_INFO_LIST.size())
                    .extracting("rank", "nickname", "point")
                    .contains(
                        Tuple.tuple(1, SOPTAMP_USER_6.getNickname(), SOPTAMP_USER_6.getTotalPoints()),
                        Tuple.tuple(2, SOPTAMP_USER_5.getNickname(), SOPTAMP_USER_5.getTotalPoints()),
                        Tuple.tuple(5, SOPTAMP_USER_2.getNickname(), SOPTAMP_USER_2.getTotalPoints()),
                        Tuple.tuple(6, SOPTAMP_USER_1.getNickname(), SOPTAMP_USER_1.getTotalPoints())
                    );
            }

            @Test
            @DisplayName("SUCCESS_동점자가 존재할 경우 순위 보장은 되지 않으며 동일 순위없이 조회됨")
            void SUCCESS_findCurrentRanks_whenTiedUsers() {
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
        }

    }

    @Test
    @DisplayName("SUCCESS_동점자가 포함된 현재 기수의 솝탬프 유저 랭킹을 정상적으로 조회함")
    void SUCCESS_findCurrentRanks() {
        //given
        given(rankCacheService.getRanking()).willReturn(Collections.emptySet());
        given(soptampUserFinder.findAllOfCurrentGeneration()).willReturn(SOPTAMP_USER_INFO_LIST);

        // when
        List<Main> result = rankFacade.findCurrentRanks();

        // then
        assertThat(result)
            .hasSize(SOPTAMP_USER_INFO_LIST.size())
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

    @Test
    @DisplayName("SUCCESS_파트별 현재 기수의 솝탬프 유저 랭킹을 정상적으로 조회함")
    void SUCCESS_findCurrentRanksByPart() {
        // given
        given(soptampUserFinder.findAllByPartAndCurrentGeneration(Part.SERVER)).willReturn(SERVER_PART_SOPTAMP_USER_INFO_LIST);

        // when
        List<Main> result = rankFacade.findCurrentRanksByPart(Part.SERVER);

        // then
       assertThat(result)
           .hasSize(SERVER_PART_SOPTAMP_USER_INFO_LIST.size())
           .extracting("rank", "nickname", "point")
           .containsExactlyInAnyOrder(
               Tuple.tuple(1, SOPTAMP_USER_6.getNickname(), SOPTAMP_USER_6.getTotalPoints()),
               Tuple.tuple(2, SOPTAMP_USER_5.getNickname(), SOPTAMP_USER_5.getTotalPoints()),
               Tuple.tuple(3, SOPTAMP_USER_1.getNickname(), SOPTAMP_USER_1.getTotalPoints())
           );
    }

    @Test
    @DisplayName("SUCCESS_파트에 soptampUser가 존재하지 않을 경우 파트별 현재 기수의 솝탬프 유저 랭킹 조회 시 빈리스트를 정상적으로 반환함")
    void SUCCESS_findCurrentRanksByPart_whenEmptyUser() {
        // given
        given(soptampUserFinder.findAllByPartAndCurrentGeneration(Part.SERVER)).willReturn(Collections.emptyList());

        // when
        List<Main> result = rankFacade.findCurrentRanksByPart(Part.SERVER);

        // then
        assertThat(result).isEmpty();
    }

    @Nested
    @DisplayName("파트별 랭킹 조회 테스트")
    class FindAllPartRanksTest{

        private List<PartRank> result;

        @BeforeEach
        void setUp(){
            // given
            given(soptampUserFinder.findAllOfCurrentGeneration()).willReturn(SOPTAMP_USER_INFO_LIST);

            //when
            result = rankFacade.findAllPartRanks();
        }


        @Test
        @DisplayName("SUCCESS 파트별 솝탬프 포인트 랭킹 조회 시 기-디-웹-아-안-서 순서대로 조회함")
        void SUCCESS_findAllPartRanks_sortedByPart() {
            //then
            assertThat(result)
                .extracting(PartRank::getPart)
                .contains(Part.PLAN.getPartName(),
                    Part.DESIGN.getPartName(),
                    Part.WEB.getPartName(),
                    Part.IOS.getPartName(),
                    Part.ANDROID.getPartName(),
                    Part.SERVER.getPartName());
        }

        @Test
        @DisplayName("SUCCESS 파트별 솝탬프 포인트가 동점일 경우 랭킹도 동점으로 조회됨")
        void SUCCESS_findAllPartRanks_whenTiedPart() {
            Long tiedPoint = SOPTAMP_USER_3.getTotalPoints();

            // then
            List<PartRank> tiedPart = result.stream()
                .filter(partRank -> partRank.getPoints().equals(tiedPoint))
                .toList();

            assertThat(tiedPart)
                .hasSize(2)
                .extracting("part", "rank", "points")
                .contains(
                    Tuple.tuple(Part.IOS.getPartName(), 2, tiedPoint),
                    Tuple.tuple(Part.DESIGN.getPartName(), 2, tiedPoint)
                );
        }

        @Test
        @DisplayName("SUCCESS 이전에 솝탬프 포인트가 동점인 파트가 존재했을 경우 다음 순위는 동점 파트 수를 건너뛰고 계산됨")
        void SUCCESS_findAllPartRanks_whenAfterTiedPart(){
            // then
            assertThat(result)
                .extracting("part", "rank")
                .contains(Tuple.tuple(Part.ANDROID.getPartName(), 4));
        }

    }

    @Nested
    @DisplayName("파트 랭크 조회 테스트")
    class FindPartRankTest{

        @BeforeEach
        void setUp(){
            given(soptampUserFinder.findAllOfCurrentGeneration()).willReturn(SOPTAMP_USER_INFO_LIST);
        }

        @Test
        @DisplayName("SUCCESS_파트 랭크를 정상적으로 조회함")
        void SUCCESS_findPartRank() {
            // when
            PartRank result = rankFacade.findPartRank(Part.SERVER);

            // then
            assertThat(result)
                .extracting("part", "rank", "points")
                .contains(Part.SERVER.getPartName(), 1, SERVER_PART_SOPTAMP_USER.stream()
                    .mapToLong(SoptampUser::getTotalPoints)
                    .sum());
        }

        @Test
        @DisplayName("SUCCESS_포인트가 동점일 경우 동일한 순위로 조회됨")
        void SUCCESS_findPartRank_whenTiedPart() {
            // when
            PartRank designResult = rankFacade.findPartRank(DESIGN);
            PartRank iosResult = rankFacade.findPartRank(IOS);

            // then
            assertThat(designResult)
                .extracting("part", "rank", "points")
                .contains(Part.DESIGN.getPartName(), 2, SOPTAMP_USER_4.getTotalPoints());

            assertThat(iosResult)
                .extracting("part", "rank", "points")
                .contains(Part.IOS.getPartName(), 2, SOPTAMP_USER_3.getTotalPoints());
        }

        @Test
        @DisplayName("SUCCESS_이전에 포인트가 동점인 파트가 존재했을 경우 다음 순위는 동점 파트 수를 건너뛰고 계산됨")
        void SUCCESS_findPartRank_whenAfterTiedPart() {
            // when
            PartRank result = rankFacade.findPartRank(ANDROID);

            // then
            assertThat(result)
                .extracting("part", "rank", "points")
                .contains(ANDROID.getPartName(), 4, SOPTAMP_USER_2.getTotalPoints());
        }

    }

    @Nested
    @DisplayName("유저 랭킹 조회 테스트")
    class FindUserRankTest{

        @BeforeEach
        public void setUp(){
            given(rankCacheService.getRanking()).willReturn(Collections.emptySet());
            given(soptampUserFinder.findAllOfCurrentGeneration()).willReturn(SOPTAMP_USER_INFO_LIST);
        }

        @Test
        @DisplayName("SUCCESS_유저 랭크를 정상적으로 조회함")
        void SUCCESS_findUserRank() {
            // when
            Long result = rankFacade.findUserRank(SOPTAMP_USER_6.getUserId());

            // then
            assertThat(result).isEqualTo(1L);
        }

        @Test
        @DisplayName("SUCCESS_동점인 유저가 존재할 경우 순위가 보장되지 않음")
        void SUCCESS_findUserRank_whenTiedUser() {
            // when
            Long resultUser3 = rankFacade.findUserRank(SOPTAMP_USER_3.getUserId());
            Long resultUser4 = rankFacade.findUserRank(SOPTAMP_USER_4.getUserId());

            // then
            assertThat(resultUser3).isBetween(3L, 4L);
            assertThat(resultUser4).isBetween(3L, 4L);
        }

    }

}