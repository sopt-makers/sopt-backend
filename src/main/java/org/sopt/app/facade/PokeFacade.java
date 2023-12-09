package org.sopt.app.facade;

import lombok.RequiredArgsConstructor;
import lombok.val;
import org.sopt.app.application.auth.PlaygroundAuthInfo;
import org.sopt.app.application.auth.PlaygroundAuthService;
import org.sopt.app.application.friend.FriendInfo;
import org.sopt.app.application.friend.FriendInfo.Friend;
import org.sopt.app.application.poke.FriendService;
import org.sopt.app.application.poke.PokeHistoryService;
import org.sopt.app.application.poke.PokeInfo;
import org.sopt.app.application.poke.PokeService;
import org.sopt.app.application.user.UserInfo;
import org.sopt.app.application.user.UserInfo.PokeProfile;
import org.sopt.app.application.user.UserInfo.UserProfile;
import org.sopt.app.application.user.UserService;
import org.sopt.app.domain.entity.PokeHistory;
import org.sopt.app.domain.entity.User;
import org.sopt.app.domain.enums.Friendship;
import org.sopt.app.presentation.poke.PokeResponse.SimplePokeProfile;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PokeFacade {

    private final PlaygroundAuthService playgroundAuthService;
    private final UserService userService;
    private final FriendService friendService;
    private final PokeService pokeService;
    private final PokeHistoryService pokeHistoryService;

    @Transactional(readOnly = true)
    public List<PokeProfile> getRecommendUserForNew(String playgroundToken, Long userPlaygroundId) {
        val playgroundUserIds = playgroundAuthService.getPlayGroundUserIds(playgroundToken);
        int RECOMMEND_USER_NUM_FOR_NEW = 6;
        val recommendUserIds = pickRandomUserIds(playgroundUserIds.getUserIds(), userPlaygroundId,
            RECOMMEND_USER_NUM_FOR_NEW
        );
        val playgroundProfiles = playgroundAuthService.getPlaygroundMemberProfiles(playgroundToken, recommendUserIds);
        val userProfiles = userService.getUserProfilesByPlaygroundIds(recommendUserIds);
        return userService.combinePokeProfileList(userProfiles, playgroundProfiles);
    }

    private List<Long> pickRandomUserIds(
        List<Long> playgroundUserIds, Long userPlaygroundId, int limitNum
    ) {
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
                val friendProfile = playgroundAuthService.getPlaygroundMemberProfiles(user.getPlaygroundToken() ,List.of(friendId)).get(0);
                val recommendUserProfiles = userService.findRandomFriendsOfFriends(userId, friendId, 2);
                val playgroundProfiles = playgroundAuthService.getPlaygroundMemberProfiles(user.getPlaygroundToken(), recommendUserProfiles.stream().map(UserProfile::getPlaygroundId).toList());
                val recommendFriendList = userService.combinePokeProfileList(recommendUserProfiles, playgroundProfiles);
                return FriendInfo.Friend.builder()
                    .id(friendId)
                    .name(friendProfile.getName())
                    .profileImage(friendProfile.getProfileImage())
                    .friendList(recommendFriendList)
                    .build();
            }
        ).toList();
    }

    @Transactional(readOnly = true)
    public Long getFirstUserIdOfPokeMeReplyYet(Long userId) {
        return pokeHistoryService.getPokeFriendIdsInOrderByMostRecent(userId).stream()
                .filter(history -> !history.getIsReply())
                .map(PokeHistory::getPokerId)
                .distinct()
                .findFirst()
                .orElse(null);
    }

    @Transactional(readOnly = true)
    public List<Long> getAllUserIdsOfPokeMe(Long userId, Pageable pageable) {
        return pokeHistoryService.getPokeFriendIdsInOrderByMostRecent(userId, pageable).stream()
                .map(PokeHistory::getPokerId)
                .distinct()
                .toList();
    }

    @Transactional
    public void applyFriendship(Long pokerUserId, Long pokedUserId) {
        Boolean userPokeBefore = pokeHistoryService.isUserPokeFriendBefore(
            pokerUserId, pokedUserId);
        Boolean friendPokeBefore = pokeHistoryService.isUserPokeFriendBefore(
            pokedUserId, pokerUserId);
        if (isFriendshipNeedToBeCreate(userPokeBefore, friendPokeBefore)) {
            friendService.createRelation(pokerUserId, pokedUserId);
        }
    }

    @Transactional
    public void pokeFriend(Long pokerUserId, Long pokedUserId, String pokeMessage) {
        pokeHistoryService.checkUserOverDailyPokeLimit(pokerUserId);

        boolean friendEachOther = friendService.isFriendEachOther(pokerUserId, pokedUserId);
        if (friendEachOther) {
            friendService.applyPokeCount(pokerUserId, pokedUserId);
        }
        pokeService.poke(pokerUserId, pokedUserId, pokeMessage);
    }

    private boolean isFriendshipNeedToBeCreate(
        Boolean isPokerPokeBefore, Boolean isPokedPokeBefore
    ) {
        if (isPokedPokeBefore && isPokerPokeBefore) {
            return false;
        }
        if (!isPokedPokeBefore && !isPokerPokeBefore) {
            return false;
        }
        return isPokedPokeBefore;
    }

    @Transactional(readOnly = true)
    public List<PokeProfile> getFriend(User user) {
        val pokedFriendIds = pokeHistoryService.getPokedFriendIds(user.getId());
        val pokeFriendIds = pokeHistoryService.getPokeFriendIds(user.getId());
        val friendId = friendService.getNotPokeFriendIdRandomly(
            user.getId(),
            pokedFriendIds, pokeFriendIds);
        val friendUserProfile = userService.getUserProfileByUserId(friendId);
        val friendPlaygroundIds = friendUserProfile.stream().map(UserProfile::getPlaygroundId).toList();
        val friendProfile = playgroundAuthService.getPlaygroundMemberProfiles(user.getPlaygroundToken(), friendPlaygroundIds);

        return userService.combinePokeProfileList(friendUserProfile, friendProfile);
    }

    @Transactional(readOnly = true)
    public List<SimplePokeProfile> getFriendByFriendship(User user, Friendship friendship) {
        List<Long> twoFriendsOfFriendship = friendService.findAllFriendsByFriendship(user.getId(), friendship.getLowerLimit(), friendship.getUpperLimit());
        return twoFriendsOfFriendship.stream()
                .limit(2)
                .map(friendId -> getPokeHistoryProfileWith(user, friendId))
                .toList();
    }

    @Transactional(readOnly = true)
    public SimplePokeProfile getPokeHistoryProfileWith(User user, Long otherUserId) {
        PokeInfo.PokedUserInfo otherUserInfo = getPokedUserInfo(user, otherUserId);
        PokeInfo.PokeDetail pokeDetail = getPokeInfo(user, otherUserId);

        return SimplePokeProfile.of(
                otherUserInfo.getUserId(),
                otherUserInfo.getProfileImage(),
                otherUserInfo.getName(),
                pokeDetail.getMessage(),
                otherUserInfo.getActivity(),
                otherUserInfo.getRelation().getPokeCount(),
                otherUserInfo.getRelation().getRelationName(),
                otherUserInfo.getMutualFriendNames(),
                otherUserInfo.getRelation().getPokeCount() == 0,
                pokeDetail.getIsReply()
        );
    }
    private PokeInfo.PokedUserInfo getPokedUserInfo(User user, Long pokedUserId) {
        val pokedUser = userService.getUserProfile(pokedUserId);
        val userProfile =  userService.getUserProfilesByPlaygroundIds(List.of(pokedUser.getPlaygroundId())).get(0);
        val playgroundMemberProfile = (PlaygroundAuthInfo.MemberProfile) playgroundAuthService.getPlaygroundMemberProfiles(user.getPlaygroundToken(), List.of(userProfile.getPlaygroundId())).get(0);
        val relationInfo = friendService.getRelationInfo(user.getId(), pokedUserId);
        val latestActivity = playgroundMemberProfile.getLatestActivity();
        val mutualFriendNames = friendService.getMutualFriendIds(user.getId(), pokedUserId).stream()
                .map(id -> {
                    UserInfo.UserProfile friendProfile = userService.getUserProfile(id);
                    return friendProfile.getName();
                })
                .toList();
        return PokeInfo.PokedUserInfo.builder()
                .userId(userProfile.getUserId())
                .name(playgroundMemberProfile.getName())
                .profileImage(playgroundMemberProfile.getProfileImage())
                .activity(
                        PokeInfo.Activity.builder()
                                .generation(Integer.parseInt(latestActivity.getGeneration()))
                                .part(latestActivity.getPart())
                                .build()
                )
                .relation(relationInfo)
                .mutualFriendNames(mutualFriendNames)
                .build();
    }

    private PokeInfo.PokeDetail getPokeInfo(User user, Long pokedUserId) {
        return pokeService.getPokeDetail(user.getId(), pokedUserId);
    }

}
