package org.sopt.app.application.friend;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import org.sopt.app.application.playground.PlaygroundAuthService;
import org.sopt.app.application.playground.dto.PlaygroundProfileInfo.*;
import org.sopt.app.application.playground.user_finder.PlaygroundUserIdsProvider;
import org.sopt.app.application.user.UserProfile;
import org.sopt.app.application.user.UserService;
import org.sopt.app.common.utils.RandomPicker;
import org.sopt.app.domain.entity.User;
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

    public RecommendedFriendsRequest recommendFriendsByTypeList(List<FriendRecommendType> typeList, int size,
        Long userId) {
        typeList = this.adjustTypeList(typeList);

        OwnPlaygroundProfile ownProfile = playgroundAuthService.getOwnPlaygroundProfile(userId);
        FriendFilter friendFilter =
                new FriendFilter(friendService.findAllFriendIdsByUserId(userId), userId);
        List<RecommendedFriendsByType> recommendedFriends = new ArrayList<>();
        for (FriendRecommendType type : typeList) {
            List<UserProfile> recommendableUserProfiles =
                    this.getRecommendableUserProfiles(type, ownProfile, friendFilter);
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
            FriendRecommendType type, OwnPlaygroundProfile ownProfile, FriendFilter friendFilter) {
        Set<Long> userIds;
        if (type == FriendRecommendType.ALL_USER) {
            userIds = Set.copyOf(userService.getAllUserIds());
        } else {
            userIds = playgroundUserIdsProvider.findPlaygroundIdsByType(ownProfile, type);
        }
        List<UserProfile> unFilteredUserProfiles = userService.getUserProfilesByUserIds(List.copyOf(userIds));
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
