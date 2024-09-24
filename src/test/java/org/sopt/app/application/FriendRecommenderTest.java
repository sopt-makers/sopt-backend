package org.sopt.app.application;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Set;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.sopt.app.application.friend.FriendRecommender;
import org.sopt.app.application.friend.FriendService;
import org.sopt.app.application.playground.PlaygroundAuthService;
import org.sopt.app.application.playground.user_finder.PlaygroundUserIdsProvider;
import org.sopt.app.application.user.UserService;
import org.sopt.app.common.fixtures.PokeFixture;
import org.sopt.app.common.fixtures.UserFixture;
import org.sopt.app.domain.enums.FriendRecommendType;
import org.sopt.app.facade.PokeFacade;
import org.sopt.app.presentation.poke.PokeResponse.RecommendedFriendsRequest;
import org.sopt.app.presentation.poke.PokeResponse.SimplePokeProfile;

class FriendRecommenderTest {

    @Mock
    private PlaygroundAuthService playgroundAuthService;
    @Mock
    private UserService userService;
    @Mock
    private FriendService friendService;
    @Mock
    private PlaygroundUserIdsProvider playgroundUserIdsProvider;
    @InjectMocks
    private FriendRecommender friendRecommender;

    @Test
    @DisplayName("SUCCESS_모든 유형의 추천 친구 조회")
    void SUCCESS_getRecommendedFriendsByAllType() {
        // given
        final Set<Long> sameMbtiPlaygroundIds = Set.of(1L);
        final List<Long> sameMbtiUserIds = List.of(11L);
        final Set<Long> sameUniversityPlaygroundIds = Set.of(2L);
        final List<Long> sameUniversityUserIds = List.of(22L);
        final Set<Long> sameGenerationPlaygroundIds = Set.of(3L);
        final List<Long> sameGenerationUserIds = List.of(33L);

        given(playgroundAuthService.getOwnPlaygroundProfile(anyString()))
                .willReturn(PokeFixture.createOwnPlaygroundProfile());
        given(friendService.findAllFriendIdsByUserId(anyLong())).willReturn(Set.of()); // 현재 친구인 유저 없음

        //== MBTI, UNIVERSITY, GENERATION별 playgroundIds를 찾는 로직 ==//
        given(playgroundUserIdsProvider.findPlaygroundIdsByType(any(), eq(FriendRecommendType.MBTI)))
                .willReturn(sameMbtiPlaygroundIds);
        given(playgroundUserIdsProvider.findPlaygroundIdsByType(any(), eq(FriendRecommendType.UNIVERSITY)))
                .willReturn(sameUniversityPlaygroundIds);
        given(playgroundUserIdsProvider.findPlaygroundIdsByType(any(), eq(FriendRecommendType.GENERATION)))
                .willReturn(sameGenerationPlaygroundIds);

        //== MBTI, UNIVERSITY, GENERATION별 playground profile을 찾는 로직 ==//
        given(playgroundAuthService.getPlaygroundMemberProfiles(anyString(), eq(List.copyOf(sameMbtiPlaygroundIds))))
                .willReturn(PokeFixture.createPlaygroundProfileList(List.copyOf(sameMbtiPlaygroundIds)));
        given(playgroundAuthService.getPlaygroundMemberProfiles(anyString(), eq(List.copyOf(sameUniversityPlaygroundIds))))
                .willReturn(PokeFixture.createPlaygroundProfileList(List.copyOf(sameUniversityPlaygroundIds)));
        given(playgroundAuthService.getPlaygroundMemberProfiles(anyString(), eq(List.copyOf(sameGenerationPlaygroundIds))))
                .willReturn(PokeFixture.createPlaygroundProfileList(List.copyOf(sameGenerationPlaygroundIds)));

        //== MBTI, UNIVERSITY, GENERATION별 user profile을 찾는 로직 ==//
        given(userService.getUserProfilesByPlaygroundIds(List.copyOf(sameMbtiPlaygroundIds)))
                .willReturn(PokeFixture.createUserProfileList(sameMbtiUserIds ,List.copyOf(sameMbtiPlaygroundIds)));
        given(userService.getUserProfilesByPlaygroundIds(List.copyOf(sameUniversityPlaygroundIds)))
                .willReturn(PokeFixture.createUserProfileList(sameUniversityUserIds ,List.copyOf(sameUniversityPlaygroundIds)));
        given(userService.getUserProfilesByPlaygroundIds(List.copyOf(sameGenerationPlaygroundIds)))
                .willReturn(PokeFixture.createUserProfileList(sameGenerationUserIds ,List.copyOf(sameGenerationPlaygroundIds)));

        // when
        RecommendedFriendsRequest result = friendRecommender.recommendFriendsByTypeList(
                List.of(FriendRecommendType.ALL), 6, UserFixture.createMyAppUser());
        List<Long> resultSameMbtiPlaygroundIds = result.getRandomInfoList().stream()
                .filter(info -> info.getRandomType() == FriendRecommendType.MBTI)
                .findFirst().get().getUserInfoList().stream().map(SimplePokeProfile::getPlaygroundId).toList();
        List<Long> resultSameUniversityPlaygroundIds = result.getRandomInfoList().stream()
                .filter(info -> info.getRandomType() == FriendRecommendType.UNIVERSITY)
                .findFirst().get().getUserInfoList().stream().map(SimplePokeProfile::getPlaygroundId).toList();
        List<Long> resultSameGenerationPlaygroundIds = result.getRandomInfoList().stream()
                .filter(info -> info.getRandomType() == FriendRecommendType.GENERATION)
                .findFirst().get().getUserInfoList().stream().map(SimplePokeProfile::getPlaygroundId).toList();

        // then
        assertEquals(3, result.getRandomInfoList().size());
        assertEquals(List.copyOf(sameMbtiPlaygroundIds), resultSameMbtiPlaygroundIds);
        assertEquals(List.copyOf(sameUniversityPlaygroundIds), resultSameUniversityPlaygroundIds);
        assertEquals(List.copyOf(sameGenerationPlaygroundIds), resultSameGenerationPlaygroundIds);
    }

    @Test
    @DisplayName("SUCCESS_요구사항1_추천할 친구가 없다면 객체를 반환하지 않음")
    void SUCCESS_getRecommendedFriendsByAllType_Requirement1() {
        // given
        final List<Long> emptyPlaygroundIds = List.of();
        final List<Long> emptyUserIds = List.of();

        given(playgroundAuthService.getOwnPlaygroundProfile(anyString()))
                .willReturn(PokeFixture.createOwnPlaygroundProfile());
        given(friendService.findAllFriendIdsByUserId(anyLong())).willReturn(Set.of()); // 현재 친구인 유저 없음
        given(userService.getAllPlaygroundIds()).willReturn(emptyPlaygroundIds); // 추천된 playground id 없음
        given(userService.getUserProfilesByPlaygroundIds(emptyPlaygroundIds))
                .willReturn(PokeFixture.createUserProfileList(emptyUserIds ,List.copyOf(emptyPlaygroundIds)));

        // when
        RecommendedFriendsRequest result = friendRecommender.recommendFriendsByTypeList(
                List.of(FriendRecommendType.ALL_USER), 6, UserFixture.createMyAppUser());

        // then
        assertTrue(result.getRandomInfoList().isEmpty());
    }

    @Test
    @DisplayName("SUCCESS_요구사항2_자기 자신은 추천 친구에서 제외되어야 함")
    void SUCCESS_getRecommendedFriendsByAllType_Requirement2() {
        // given
        final List<Long> playgroundIds = List.of(UserFixture.myPlaygroundId);
        final List<Long> userIds = List.of(UserFixture.myAppUserId);

        given(playgroundAuthService.getOwnPlaygroundProfile(anyString()))
                .willReturn(PokeFixture.createOwnPlaygroundProfile());
        given(friendService.findAllFriendIdsByUserId(anyLong())).willReturn(Set.of()); // 현재 친구인 유저 없음

        //== playgroundIds에 자신의 playgroundId만 포함됨 ==//
        given(userService.getAllPlaygroundIds()).willReturn(playgroundIds);
        given(userService.getUserProfilesByPlaygroundIds(playgroundIds)).willReturn(
                PokeFixture.createUserProfileList(userIds, playgroundIds));

        // when
        RecommendedFriendsRequest result = friendRecommender.recommendFriendsByTypeList(
                List.of(FriendRecommendType.ALL_USER), 6, UserFixture.createMyAppUser());

        // then
        assertTrue(result.getRandomInfoList().isEmpty());
    }

    @Test
    @DisplayName("SUCCESS_요구사항3_플그 아이디는 있지만 앱 아이디가 없는 유저는 추천하지 않도록 필터링")
    void SUCCESS_getRecommendedFriendsByAllType_Requirement3() {
        // given
        final List<Long> playgroundIds = List.of(1L);

        given(playgroundAuthService.getOwnPlaygroundProfile(anyString()))
                .willReturn(PokeFixture.createOwnPlaygroundProfile());
        given(friendService.findAllFriendIdsByUserId(anyLong())).willReturn(Set.of()); // 현재 친구인 유저 없음
        given(userService.getAllPlaygroundIds()).willReturn(playgroundIds); // playgroundId 존재
        given(userService.getUserProfilesByPlaygroundIds(playgroundIds)).willReturn(List.of());// 앱 아이디가 없어 userProfile을 반환하지 않음

        // when
        RecommendedFriendsRequest result = friendRecommender.recommendFriendsByTypeList(
                List.of(FriendRecommendType.ALL_USER), 6, UserFixture.createMyAppUser());

        // then
        assertTrue(result.getRandomInfoList().isEmpty());
    }

    @Test
    @DisplayName("SUCCESS_요구사항4_이미 친구인 유저는 추천 친구에서 제외함 ")
    void SUCCESS_getRecommendedFriendsByAllType_Requirement4() {
        // given
        final List<Long> playgroundIds = List.of(1L, 2L);
        final List<Long> userIds = List.of(11L, 22L);

        given(playgroundAuthService.getOwnPlaygroundProfile(anyString()))
                .willReturn(PokeFixture.createOwnPlaygroundProfile());
        given(friendService.findAllFriendIdsByUserId(anyLong())).willReturn(Set.of(11L)); // 11번 유저는 이미 친구

        given(userService.getAllPlaygroundIds()).willReturn(playgroundIds);
        given(userService.getUserProfilesByPlaygroundIds(any())).willReturn(
                PokeFixture.createUserProfileList(userIds, playgroundIds));

        // when
        ArgumentCaptor<List<Long>> ac = ArgumentCaptor.forClass(List.class);
        when(playgroundAuthService.getPlaygroundMemberProfiles(anyString(), ac.capture())).
                thenReturn(PokeFixture.createPlaygroundProfileList(List.of(2L)));
        RecommendedFriendsRequest result = friendRecommender.recommendFriendsByTypeList(
                List.of(FriendRecommendType.ALL_USER), 6, UserFixture.createMyAppUser());

        // then
        assertEquals(2L, ac.getValue().get(0));
        assertEquals(22L, result.getRandomInfoList().get(0).getUserInfoList().get(0).getUserId());
    }

    // TODO: 자신의 유형 값이 null이면 객체를 반환하지 않아야 하는 테스트 다른 객체로 책임 전달
    @Test
    @DisplayName("SUCCESS_요구사항5_자신의 유형 값이 null이면 객체를 반환하지 않음")
    void SUCCESS_getRecommendedFriendsByAllType_Requirement5() {

    }

}
