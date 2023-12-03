package org.sopt.app.facade;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.val;
import org.sopt.app.application.auth.PlaygroundAuthService;
import org.sopt.app.application.friend.FriendInfo;
import org.sopt.app.application.friend.FriendInfo.Friend;
import org.sopt.app.application.friend.FriendService;
import org.sopt.app.application.user.UserInfo.PokeProfile;
import org.sopt.app.application.user.UserInfo.UserProfile;
import org.sopt.app.application.user.UserService;
import org.sopt.app.domain.entity.User;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class PokeFacade {
    private final PlaygroundAuthService playgroundAuthService;
    private final UserService userService;
    private final FriendService friendService;

    public List<PokeProfile> getRecommendUserForNew(String playgroundToken, Long userPlaygroundId) {
        val playgroundUserIds = playgroundAuthService.getPlayGroundUserIds(playgroundToken);
        int RECOMMEND_USER_NUM_FOR_NEW = 6;
        val recommendUserIds = pickRandomUserIds(playgroundUserIds.getUserIds(), userPlaygroundId,
            RECOMMEND_USER_NUM_FOR_NEW
        );
        val playgroundProfiles = playgroundAuthService.getPlaygroundProfiles(recommendUserIds);
        val userProfiles = userService.getUserProfiles(recommendUserIds);
        return userService.combinePokeProfileList(userProfiles, playgroundProfiles);
    }

    private List<Long> pickRandomUserIds(List<Long> playgroundUserIds, Long userPlaygroundId, int limitNum) {
        List<Long> copiedList = new ArrayList<>(playgroundUserIds);
        copiedList.remove(userPlaygroundId);
        Collections.shuffle(copiedList, new Random());
        return copiedList.stream().limit(limitNum).collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<Friend> getRecommendFriendsOfUsersFriend(User user) {
        val userId = user.getId();
        val friendIds = friendService.findAllFriendIdsByUserIdRandomly(userId, 2);
        return friendIds.stream().map(
            friendId -> {
                val friendProfile = playgroundAuthService.getPlaygroundProfiles(List.of(friendId)).get(0);
                val recommendUserProfiles = userService.findRandomFriendsOfFriends(userId, friendId, 2);
                val playgroundProfiles = playgroundAuthService.getPlaygroundProfiles(recommendUserProfiles.stream().map(UserProfile::getPlaygroundId).toList());
                val recommendFriendList = userService.combinePokeProfileList(recommendUserProfiles, playgroundProfiles);
                return FriendInfo.Friend.builder()
                    .friendId(friendId)
                    .friendName(friendProfile.getName())
                    .friendProfileImage(friendProfile.getProfileImage())
                    .friendList(recommendFriendList)
                    .build();
            }
        ).toList();
    }
}
