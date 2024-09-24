package org.sopt.app.application.friend;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import org.sopt.app.application.playground.PlaygroundAuthService;
import org.sopt.app.application.playground.dto.PlaygroundProfileInfo.OwnPlaygroundProfile;
import org.sopt.app.application.playground.dto.PlaygroundProfileInfo.PlaygroundProfile;
import org.sopt.app.application.playground.user_finder.PlaygroundUserIdsProvider;
import org.sopt.app.application.user.UserInfo.UserProfile;
import org.sopt.app.application.user.UserService;
import org.sopt.app.common.utils.RandomPicker;
import org.sopt.app.domain.entity.User;
import org.sopt.app.domain.enums.FriendRecommendType;
import org.sopt.app.presentation.poke.PokeResponse.RecommendedFriendsByType;
import org.sopt.app.presentation.poke.PokeResponse.RecommendedFriendsRequest;
import org.sopt.app.presentation.poke.PokeResponse.SimplePokeProfile;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class FriendRecommender {

    private final PlaygroundAuthService playgroundAuthService;
    private final UserService userService;
    private final FriendService friendService;
    private final PlaygroundUserIdsProvider playgroundUserIdsProvider;

    public RecommendedFriendsRequest recommendFriendsByTypeList(
            List<FriendRecommendType> typeList, int size, User user) {
        typeList = this.adjustTypeList(typeList);

        OwnPlaygroundProfile ownProfile = playgroundAuthService.getOwnPlaygroundProfile(user.getPlaygroundToken());
        FriendFilter friendFilter =
                new FriendFilter(friendService.findAllFriendIdsByUserId(user.getId()), user.getId());
        List<RecommendedFriendsByType> recommendedFriends = new ArrayList<>();
        for (FriendRecommendType type : typeList) {
            List<UserProfile> recommendableUserProfiles =
                    this.getRecommendableUserProfiles(type, ownProfile, friendFilter);
            if (!recommendableUserProfiles.isEmpty()) {
                List<UserProfile> pickedUserProfiles = RandomPicker.pickRandom(recommendableUserProfiles, size);
                List<SimplePokeProfile> simplePokeProfiles =
                        this.convertUserProfilesToSimplePokeProfiles(pickedUserProfiles, user);

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
            List<UserProfile> pickedUserProfiles, User user) {
        List<Long> pickedPlaygroundIds = pickedUserProfiles.stream().map(UserProfile::getPlaygroundId).toList();
        List<PlaygroundProfile> pickedPlaygroundProfiles = playgroundAuthService.getPlaygroundMemberProfiles(
                user.getPlaygroundToken(), pickedPlaygroundIds);
        return this.makeSimplePokeProfilesForNotFriend(pickedPlaygroundProfiles, pickedUserProfiles);
    }

    private List<SimplePokeProfile> makeSimplePokeProfilesForNotFriend(
            List<PlaygroundProfile> playgroundProfiles, List<UserProfile> userProfiles) {

        return userProfiles.stream().map(userProfile -> {
            PlaygroundProfile playgroundProfile = playgroundProfiles.stream()
                    .filter(profile -> profile.getMemberId().equals(userProfile.getPlaygroundId()))
                    .findFirst().orElseThrow();

            return SimplePokeProfile.createNonFriendPokeProfile(
                    userProfile.getUserId(),
                    userProfile.getPlaygroundId(),
                    playgroundProfile.getProfileImage(),
                    userProfile.getName(),
                    playgroundProfile.getLatestActivity().getGeneration(),
                    playgroundProfile.getLatestActivity().getPart()
            );
        }).toList();
    }

    private List<UserProfile> getRecommendableUserProfiles(
            FriendRecommendType type, OwnPlaygroundProfile ownProfile, FriendFilter friendFilter) {
        Set<Long> playgroundIds;
        if (type == FriendRecommendType.ALL_USER) {
            playgroundIds = Set.copyOf(userService.getAllPlaygroundIds());
        } else {
            playgroundIds = playgroundUserIdsProvider.findPlaygroundIdsByType(ownProfile, type);
        }
        List<UserProfile> unFilteredUserProfiles = userService.getUserProfilesByPlaygroundIds(List.copyOf(playgroundIds));
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
