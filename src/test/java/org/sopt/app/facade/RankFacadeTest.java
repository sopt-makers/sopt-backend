package org.sopt.app.facade;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;
import static org.sopt.app.common.fixtures.SoptampUserFixture.CURRENT_GENERATION;
import static org.sopt.app.common.fixtures.SoptampUserFixture.PART_MEMBER_COUNT_MAP;
import static org.sopt.app.common.fixtures.SoptampUserFixture.SERVER_PART_SOPTAMP_USER;
import static org.sopt.app.common.fixtures.SoptampUserFixture.SERVER_PART_SOPTAMP_USER_INFO_LIST;
import static org.sopt.app.common.fixtures.SoptampUserFixture.SOPTAMP_PROFILE_MESSAGE_CACHE;
import static org.sopt.app.common.fixtures.SoptampUserFixture.SOPTAMP_SCORE_CACHE;
import static org.sopt.app.common.fixtures.SoptampUserFixture.SOPTAMP_USER_1;
import static org.sopt.app.common.fixtures.SoptampUserFixture.SOPTAMP_USER_2;
import static org.sopt.app.common.fixtures.SoptampUserFixture.SOPTAMP_USER_3;
import static org.sopt.app.common.fixtures.SoptampUserFixture.SOPTAMP_USER_4;
import static org.sopt.app.common.fixtures.SoptampUserFixture.SOPTAMP_USER_5;
import static org.sopt.app.common.fixtures.SoptampUserFixture.SOPTAMP_USER_6;
import static org.sopt.app.common.fixtures.SoptampUserFixture.SOPTAMP_USER_INFO_LIST;
import static org.sopt.app.domain.enums.Part.ANDROID;
import static org.sopt.app.domain.enums.Part.DESIGN;
import static org.sopt.app.domain.enums.Part.IOS;

import java.math.BigDecimal;
import java.math.RoundingMode;
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
import org.sopt.app.application.rank.AuthPartMemberCountReader;
import org.sopt.app.application.rank.RankCacheService;
import org.sopt.app.application.soptamp.SoptampPointInfo.Main;
import org.sopt.app.application.soptamp.SoptampPointInfo.PartRank;
import org.sopt.app.application.soptamp.SoptampUserFinder;
import org.sopt.app.domain.entity.soptamp.SoptampUser;
import org.sopt.app.domain.enums.Part;
import org.sopt.app.domain.enums.SoptPart;

@ExtendWith(MockitoExtension.class)
class RankFacadeTest {

    @Mock
    private SoptampUserFinder soptampUserFinder;

    @Mock
    private RankCacheService rankCacheService;

    @Mock
    private AuthPartMemberCountReader authPartMemberCountReader;

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
                when(rankCacheService.getRanking()).thenReturn(SOPTAMP_SCORE_CACHE);
                when(rankCacheService.getUserInfo(anyLong()))
                    .thenAnswer(invocation -> {
                        Long userId  = invocation.getArgument(0);
                        return SOPTAMP_PROFILE_MESSAGE_CACHE.get(userId);
                    });
//
//                given(rankCacheService.getRanking()).willReturn(SOPTAMP_SCORE_CACHE);
//                given(rankCacheService.getUserInfo(anyLong()))
//                    .willAnswer(invocation -> {
//                        Long userId  = invocation.getArgument(0);
//                        return SOPTAMP_PROFILE_MESSAGE_CACHE.get(userId);
//                    });

                // when
                result = rankFacade.findCurrentRanks();
            }

            @Test
            @DisplayName("SUCCESS_현재 기수의 솝탬프 유저 랭킹을 정상적으로 조회함")
            void SUCCESS_findCurrentRanks() {
                assertThat(result)
                    .hasSize(SOPTAMP_USER_INFO_LIST.size())
                    .extracting(Main::getRank, Main::getNickname, Main::getPoint)
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
                    .extracting(Main::getNickname, Main::getPoint)
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
        class FindCurrentRanksWithoutCache{

            private List<Main> result;

            @BeforeEach
            void setUp(){
                // given
                when(rankCacheService.getRanking()).thenReturn(Collections.emptySet());
                when(soptampUserFinder.findAllOfCurrentGeneration()).thenReturn(SOPTAMP_USER_INFO_LIST);

                // when
                result = rankFacade.findCurrentRanks();
            }

            @Test
            @DisplayName("SUCCESS_현재 기수의 솝탬프 유저 랭킹을 정상적으로 조회함")
            void SUCCESS_findCurrentRanks() {
                assertThat(result)
                    .hasSize(SOPTAMP_USER_INFO_LIST.size())
                    .extracting(Main::getRank, Main::getNickname, Main::getPoint)
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
                    .extracting(Main::getNickname, Main::getPoint)
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
        when(rankCacheService.getRanking()).thenReturn(Collections.emptySet());
        when(soptampUserFinder.findAllOfCurrentGeneration()).thenReturn(SOPTAMP_USER_INFO_LIST);

        // when
        List<Main> result = rankFacade.findCurrentRanks();

        // then
        assertThat(result)
            .hasSize(SOPTAMP_USER_INFO_LIST.size())
            .extracting(Main::getRank, Main::getNickname, Main::getPoint)
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
            .extracting(Main::getNickname, Main::getPoint)
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
        when(soptampUserFinder.findAllByPartAndCurrentGeneration(Part.SERVER)).thenReturn(SERVER_PART_SOPTAMP_USER_INFO_LIST);

        // when
        List<Main> result = rankFacade.findCurrentRanksByPart(Part.SERVER);

        // then
        assertThat(result)
           .hasSize(SERVER_PART_SOPTAMP_USER_INFO_LIST.size())
           .extracting(Main::getRank, Main::getNickname, Main::getPoint)
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
        when(soptampUserFinder.findAllByPartAndCurrentGeneration(Part.SERVER)).thenReturn(Collections.emptyList());

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
            when(soptampUserFinder.findAllOfCurrentGeneration()).thenReturn(SOPTAMP_USER_INFO_LIST);
            when(soptampUserFinder.getCurrentGeneration()).thenReturn(CURRENT_GENERATION);
            when(authPartMemberCountReader.getCurrentGenerationPartMemberCounts(CURRENT_GENERATION))
                .thenReturn(PART_MEMBER_COUNT_MAP);

            //when
            result = rankFacade.findAllPartRanks();
        }

        @Test
        @DisplayName("SUCCESS 파트별 솝탬프 포인트 랭킹 조회 시 기-디-웹-아-안-서 순서대로 조회함")
        void SUCCESS_findAllPartRanks_sortedByPart() {
            BigDecimal planPointDecimal = BigDecimal.ZERO.setScale(2);
            BigDecimal designPointDecimal = BigDecimal.valueOf(SOPTAMP_USER_4.getTotalPoints()).divide(BigDecimal.valueOf(PART_MEMBER_COUNT_MAP.get(SoptPart.DESIGN)), 2, RoundingMode.HALF_UP);
            BigDecimal webPointDecimal = BigDecimal.ZERO.setScale(2);
            BigDecimal iosPointDecimal = BigDecimal.valueOf(SOPTAMP_USER_3.getTotalPoints()).divide(BigDecimal.valueOf(PART_MEMBER_COUNT_MAP.get(SoptPart.IOS)), 2, RoundingMode.HALF_UP);
            BigDecimal androidPointDecimal = BigDecimal.valueOf(SOPTAMP_USER_2.getTotalPoints()).divide(BigDecimal.valueOf(PART_MEMBER_COUNT_MAP.get(SoptPart.ANDROID)), 2, RoundingMode.HALF_UP);
            BigDecimal serverPointDecimal = BigDecimal.valueOf(SERVER_PART_SOPTAMP_USER.stream().mapToLong(SoptampUser::getTotalPoints).sum()).divide(BigDecimal.valueOf(PART_MEMBER_COUNT_MAP.get(SoptPart.SERVER)), 2, RoundingMode.HALF_UP);

            //then
            assertThat(result).extracting(PartRank::getPart, PartRank::getRank, PartRank::getPoints, PartRank::getPointsDecimal)
                .containsExactly(
                    Tuple.tuple(Part.PLAN.getPartName(), 5, 0L, planPointDecimal),
                    Tuple.tuple(Part.DESIGN.getPartName(), 2, 30L, designPointDecimal),
                    Tuple.tuple(Part.WEB.getPartName(), 5, 0L, webPointDecimal),
                    Tuple.tuple(Part.IOS.getPartName(), 2, 30L, iosPointDecimal),
                    Tuple.tuple(Part.ANDROID.getPartName(), 4, 20L, androidPointDecimal),
                    Tuple.tuple(Part.SERVER.getPartName(), 1, 40L, serverPointDecimal)
                );
        }

        @Test
        @DisplayName("SUCCESS 파트별 솝탬프 포인트가 동점일 경우 랭킹도 동점으로 조회됨")
        void SUCCESS_findAllPartRanks_whenTiedPart() {
            BigDecimal tiedPointDecimal = BigDecimal.valueOf(SOPTAMP_USER_3.getTotalPoints()).divide(BigDecimal.valueOf(PART_MEMBER_COUNT_MAP.get(SoptPart.IOS)), 2, RoundingMode.HALF_UP);

            // then
            List<PartRank> tiedPart = result.stream()
                .filter(partRank -> partRank.getPointsDecimal().compareTo(tiedPointDecimal) == 0)
                .toList();

            assertThat(tiedPart)
                .hasSize(2)
                .extracting(PartRank::getPart, PartRank::getRank, PartRank::getPoints, PartRank::getPointsDecimal)
                .contains(
                    Tuple.tuple(Part.IOS.getPartName(), 2, 30L, tiedPointDecimal),
                    Tuple.tuple(Part.DESIGN.getPartName(), 2, 30L, tiedPointDecimal)
                );
        }

        @Test
        @DisplayName("SUCCESS 이전에 솝탬프 포인트가 동점인 파트가 존재했을 경우 다음 순위는 동점 파트 수를 건너뛰고 계산됨")
        void SUCCESS_findAllPartRanks_whenAfterTiedPart(){
            BigDecimal androidPointDecimal = BigDecimal.valueOf(SOPTAMP_USER_2.getTotalPoints()).divide(BigDecimal.valueOf(PART_MEMBER_COUNT_MAP.get(SoptPart.ANDROID)), 2, RoundingMode.HALF_UP);

            // then
            assertThat(result)
                .extracting(PartRank::getPart, PartRank::getRank, PartRank::getPoints, PartRank::getPointsDecimal)
                .contains(Tuple.tuple(Part.ANDROID.getPartName(), 4, 20L, androidPointDecimal));
        }

    }

    @Nested
    @DisplayName("파트 랭크 조회 테스트")
    class FindPartRankTest{

        @BeforeEach
        void setUp(){
            when(soptampUserFinder.findAllOfCurrentGeneration()).thenReturn(SOPTAMP_USER_INFO_LIST);
            when(soptampUserFinder.getCurrentGeneration()).thenReturn(CURRENT_GENERATION);
            when(authPartMemberCountReader.getCurrentGenerationPartMemberCounts(CURRENT_GENERATION))
                .thenReturn(PART_MEMBER_COUNT_MAP);
        }

        @Test
        @DisplayName("SUCCESS_파트 랭크를 정상적으로 조회함")
        void SUCCESS_findPartRank() {
            // when
            PartRank result = rankFacade.findPartRank(Part.SERVER);

            BigDecimal serverPointDecimal = BigDecimal.valueOf(SERVER_PART_SOPTAMP_USER.stream().mapToLong(SoptampUser::getTotalPoints).sum())
                .divide(BigDecimal.valueOf(PART_MEMBER_COUNT_MAP.get(SoptPart.SERVER)), 2, RoundingMode.HALF_UP);

            // then
            assertThat(result)
                .extracting(PartRank::getPart, PartRank::getRank, PartRank::getPoints, PartRank::getPointsDecimal)
                .contains(Part.SERVER.getPartName(), 1, 40L, serverPointDecimal);
        }

        @Test
        @DisplayName("SUCCESS_포인트가 동점일 경우 동일한 순위로 조회됨")
        void SUCCESS_findPartRank_whenTiedPart() {
            // when
            PartRank designResult = rankFacade.findPartRank(DESIGN);
            PartRank iosResult = rankFacade.findPartRank(IOS);

            BigDecimal designPointDecimal = BigDecimal.valueOf(SOPTAMP_USER_4.getTotalPoints()).divide(BigDecimal.valueOf(PART_MEMBER_COUNT_MAP.get(SoptPart.DESIGN)), 2, RoundingMode.HALF_UP);
            BigDecimal iosPointDecimal = BigDecimal.valueOf(SOPTAMP_USER_3.getTotalPoints()).divide(BigDecimal.valueOf(PART_MEMBER_COUNT_MAP.get(SoptPart.IOS)), 2, RoundingMode.HALF_UP);

            // then
            assertThat(designResult)
                .extracting(PartRank::getPart, PartRank::getRank, PartRank::getPoints, PartRank::getPointsDecimal)
                .contains(Part.DESIGN.getPartName(), 2, 30L, designPointDecimal);

            assertThat(iosResult)
                .extracting(PartRank::getPart, PartRank::getRank, PartRank::getPoints, PartRank::getPointsDecimal)
                .contains(Part.IOS.getPartName(), 2, 30L, iosPointDecimal);
        }

        @Test
        @DisplayName("SUCCESS_이전에 포인트가 동점인 파트가 존재했을 경우 다음 순위는 동점 파트 수를 건너뛰고 계산됨")
        void SUCCESS_findPartRank_whenAfterTiedPart() {
            // when
            PartRank result = rankFacade.findPartRank(ANDROID);

            BigDecimal androidPointDecimal = BigDecimal.valueOf(SOPTAMP_USER_2.getTotalPoints()).divide(BigDecimal.valueOf(PART_MEMBER_COUNT_MAP.get(SoptPart.ANDROID)), 2, RoundingMode.HALF_UP);

            // then
            assertThat(result)
                .extracting(PartRank::getPart, PartRank::getRank, PartRank::getPoints, PartRank::getPointsDecimal)
                .contains(ANDROID.getPartName(), 4, 20L, androidPointDecimal);
        }
    }

    @Nested
    @DisplayName("유저 랭킹 조회 테스트")
    class FindUserRankTest{

        @Nested
        @DisplayName("캐시가 존재하는 경우")
        class FindUserRankTestWithCache{
            @BeforeEach
            public void setUp() {
                when(rankCacheService.getRanking()).thenReturn(SOPTAMP_SCORE_CACHE);
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

        @Nested
        @DisplayName("캐시가 존재하지 않는 경우")
        class FindUserRankTestWithoutCache {

            @BeforeEach
            public void setUp() {
                when(rankCacheService.getRanking()).thenReturn(Collections.emptySet());
                when(soptampUserFinder.findAllOfCurrentGeneration()).thenReturn(SOPTAMP_USER_INFO_LIST);
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
}
