package org.sopt.app.application.friend;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import lombok.RequiredArgsConstructor;

import org.sopt.app.application.platform.PlatformService;
import org.sopt.app.application.playground.PlaygroundAuthService;
import org.sopt.app.application.playground.dto.PlaygroundProfileInfo.*;
import org.sopt.app.application.playground.user_finder.PlaygroundUserIdsProvider;
import org.sopt.app.application.user.UserProfile;
import org.sopt.app.application.user.UserService;
import org.sopt.app.common.utils.RandomPicker;
import org.sopt.app.domain.enums.FriendRecommendType;
import org.sopt.app.presentation.poke.PokeResponse.*;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class FriendRecommender {

    private final PlaygroundAuthService playgroundAuthService;
    private final UserService userService;
    private final FriendService friendService;
    private final PlaygroundUserIdsProvider playgroundUserIdsProvider;
    private final PlatformService platformService;

    private static final int MAX_OVERSAMPLE = 50;

    public RecommendedFriendsRequest recommendFriendsByTypeList(List<FriendRecommendType> typeList, int size,
        Long userId) {
        typeList = this.adjustTypeList(typeList);

        OwnPlaygroundProfile ownProfile = playgroundAuthService.getOwnPlaygroundProfile(userId);
        FriendFilter friendFilter =
                new FriendFilter(friendService.findAllFriendIdsByUserId(userId), userId);
        List<RecommendedFriendsByType> recommendedFriends = new ArrayList<>();
        for (FriendRecommendType type : typeList) {
            List<UserProfile> recommendableUserProfiles =
                    this.getRecommendableUserProfiles(type, ownProfile, friendFilter, size);
            if (!recommendableUserProfiles.isEmpty()) {
                List<UserProfile> pickedUserProfiles = RandomPicker.pickRandom(recommendableUserProfiles, size);
                List<SimplePokeProfile> simplePokeProfiles =
                        this.convertUserProfilesToSimplePokeProfiles(pickedUserProfiles);

                recommendedFriends.add(RecommendedFriendsByType.builder()
                        .randomType(type)
                        .randomTitle(type.getRecommendTitle())
                        .userInfoList(simplePokeProfiles)
                        .build());
            }
        }
        return new RecommendedFriendsRequest(recommendedFriends);
    }

    private List<SimplePokeProfile> convertUserProfilesToSimplePokeProfiles(
            List<UserProfile> pickedUserProfiles) {
        List<Long> pickedPlaygroundIds = pickedUserProfiles.stream().map(UserProfile::getUserId).toList();
        List<PlaygroundProfile> pickedPlaygroundProfiles = playgroundAuthService.getPlaygroundMemberProfiles(pickedPlaygroundIds);
        return this.makeSimplePokeProfilesForNotFriend(pickedPlaygroundProfiles, pickedUserProfiles);
    }

    private List<SimplePokeProfile> makeSimplePokeProfilesForNotFriend(
            List<PlaygroundProfile> playgroundProfiles, List<UserProfile> userProfiles
    ) {
        return userProfiles.stream()
                .map(userProfile -> playgroundProfiles.stream()
                        .filter(profile -> profile.getMemberId().equals(userProfile.getUserId()))
                        .findFirst()
                        .map(playgroundProfile ->
                                SimplePokeProfile.createNonFriendPokeProfile(playgroundProfile, userProfile))
                )
                .filter(Optional::isPresent).map(Optional::get)
                .toList();
    }

    private List<UserProfile> getRecommendableUserProfiles(
            FriendRecommendType type, OwnPlaygroundProfile ownProfile, FriendFilter friendFilter, int size) {
        // size 가드 : 이상값 들어오면 500 대신 빈 리스트
        if (size <= 0) return List.of();

        // ALL_USER 아닐 경우 playground에서 후보 id 수집
        Set<Long> userIds;
        if (type == FriendRecommendType.ALL_USER) {
            userIds = Set.copyOf(userService.getAllUserIds());
        } else {
            userIds = playgroundUserIdsProvider.findPlaygroundIdsByType(ownProfile, type);
        }
        if (userIds.isEmpty()) return List.of();

        List<Long> candidates = new ArrayList<>(userIds);

        // oversample: size*5 (최대 50). 플랫폼 요청 URI 길이 보호(414 방지), friendFilter 탈락 대비 여유 확보 목적.
        int oversampleCount = Math.min(MAX_OVERSAMPLE, size *5);
        oversampleCount = Math.min(oversampleCount, candidates.size());

        // 플랫폼 조회 전 ID를 oversampleCount 만큼만 추출 (URI 길이 보호, 414 방지)
        List<Long> toLookup = (candidates.size() <= oversampleCount)
            ? candidates
            : RandomPicker.pickRandom(candidates, oversampleCount);

        // 플랫폼에서 유저 정보 조회
        List <UserProfile> unFilteredUserProfiles = platformService.getPlatformUserInfosResponse(List.copyOf(toLookup))
            .stream()
            .map(user -> UserProfile.of((long)user.userId(), user.name()))
            .toList();

        // 이미 친구인 유저 제외(앱 내 filter)
        return friendFilter.excludeAlreadyFriendUserIds(unFilteredUserProfiles);
    }

    private List<FriendRecommendType> adjustTypeList(List<FriendRecommendType> typeList) {
        if (typeList == null || typeList.isEmpty()) {
            return List.of(FriendRecommendType.ALL_USER);
        }

        if (typeList.contains(FriendRecommendType.ALL)) {
            return List.of(FriendRecommendType.GENERATION, FriendRecommendType.MBTI, FriendRecommendType.UNIVERSITY);
        }
        return typeList;
    }
}
