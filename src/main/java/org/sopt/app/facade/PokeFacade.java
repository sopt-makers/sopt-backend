package org.sopt.app.facade;

import static org.sopt.app.application.poke.PokeInfo.NEW_FRIEND_MANY_MUTUAL;
import static org.sopt.app.application.poke.PokeInfo.NEW_FRIEND_NO_MUTUAL;
import static org.sopt.app.application.poke.PokeInfo.NEW_FRIEND_ONE_MUTUAL;

import java.util.*;
import lombok.RequiredArgsConstructor;
import lombok.val;
import org.sopt.app.application.friend.FriendRecommender;
import org.sopt.app.application.playground.PlaygroundAuthService;
import org.sopt.app.application.friend.FriendService;
import org.sopt.app.application.poke.*;
import org.sopt.app.application.poke.PokeInfo.PokeHistoryInfo;
import org.sopt.app.application.user.UserService;
import org.sopt.app.domain.entity.poke.PokeHistory;
import org.sopt.app.domain.entity.User;
import org.sopt.app.domain.enums.FriendRecommendType;
import org.sopt.app.domain.enums.Friendship;
import org.sopt.app.presentation.poke.PokeResponse.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class PokeFacade {

    private final PlaygroundAuthService playgroundAuthService;
    private final UserService userService;
    private final FriendService friendService;
    private final FriendRecommender friendRecommender;
    private final PokeService pokeService;
    private final PokeHistoryService pokeHistoryService;
    private final PokeMessageService pokeMessageService;

    @Transactional(readOnly = true)
    public List<PokeMessage> getPokingMessages(String type) {
        val messages = pokeMessageService.pickRandomMessageByTypeOf(type);
        val fixedMessage = pokeMessageService.getFixedMessage();
        messages.add(fixedMessage);

        return messages.stream().map(messagesDetail ->
                new PokeMessage(messagesDetail.getId(), messagesDetail.getContent())
        ).toList();
    }

    public String getPokingMessageHeader(String type) {
        return pokeMessageService.getMessagesHeaderComment(type);
    }

    @Transactional(readOnly = true)
    public SimplePokeProfile getMostRecentPokeMeHistory(User user) {
        List<Long> pokeMeUserIds = pokeHistoryService.getPokeMeUserIds(user.getId());
        Optional<PokeHistory> mostRecentPokeMeHistory = pokeMeUserIds.stream()
                .filter(userService::isUserExist)
                .map(pokeMeUserId ->
                        pokeHistoryService.getAllLatestPokeHistoryFromTo(pokeMeUserId, user.getId()).get(0)
                )
                .filter(pokeHistory -> !pokeHistory.getIsReply())
                .max(Comparator.comparing(PokeHistory::getCreatedAt));
        return mostRecentPokeMeHistory
                .map(pokeHistory -> getPokeHistoryProfile(
                        user, pokeHistory.getPokerId(), pokeHistory.getId()))
                .orElse(null);

    }

    @Transactional(readOnly = true)
    public PokeToMeHistoryList getAllPokeMeHistory(User user, Pageable pageable) {
        List<Long> pokeMeUserIds = pokeHistoryService.getPokeMeUserIds(user.getId());
        List<Long> latestHistoryIds = pokeMeUserIds.stream()
                .filter(userService::isUserExist)
                .map(pokeMeUserId ->
                        pokeHistoryService.getAllLatestPokeHistoryFromTo(pokeMeUserId, user.getId())
                                .get(0).getId()
                )
                .toList();
        Page<PokeHistory> pokedHistories = pokeHistoryService.getAllLatestPokeHistoryIn(latestHistoryIds, pageable);
        val size = pokedHistories.getSize();
        val totalPageSize = size / pageable.getPageSize();
        List<SimplePokeProfile> pokeToMeHistories = pokedHistories.stream()
                .map(pokeHistory -> getPokeHistoryProfile(user, pokeHistory.getPokerId(), pokeHistory.getId()))
                .distinct()
                .toList();
        return PokeToMeHistoryList.builder()
                .history(pokeToMeHistories)
                .totalPageSize(totalPageSize)
                .pageNum(pokedHistories.getNumber())
                .pageSize(pageable.getPageSize())
                .build();
    }


    @Transactional
    public Long pokeFriend(Long pokerUserId, Long pokedUserId, String pokeMessage, Boolean isAnonymous) {
        pokeHistoryService.checkDuplicate(pokerUserId, pokedUserId);
        PokeHistory newPoke = pokeService.poke(pokerUserId, pokedUserId, pokeMessage, isAnonymous);

        applyFriendship(pokerUserId, pokedUserId);
        return newPoke.getId();
    }

    private void applyFriendship(Long pokerUserId, Long pokedUserId) {
        boolean friendEachOther = friendService.isFriendEachOther(pokerUserId, pokedUserId);
        if (friendEachOther) {
            friendService.applyPokeCount(pokerUserId, pokedUserId);
            return;
        }
        boolean userNotPokeBefore = pokeHistoryService.getAllOfPokeBetween(pokerUserId, pokedUserId).isEmpty();
        if (!userNotPokeBefore) {
            friendService.registerFriendshipOf(pokerUserId, pokedUserId);
        }
    }

    @Transactional(readOnly = true)
    public List<SimplePokeProfile> getFriend(User user) {
        Long userId = user.getId();
        val friendId = friendService.getPokeFriendIdRandomly(userId);

        val friendUserProfile = userService.getUserProfileOrElseThrow(friendId);
        val friendProfile = playgroundAuthService.getPlaygroundMemberProfiles(
                user.getPlaygroundToken(), List.of(friendUserProfile.getPlaygroundId())).get(0);
        val friendRelationInfo = friendService.getRelationInfo(userId, friendId);

        return List.of(
                SimplePokeProfile.of(
                        friendUserProfile.getUserId(),
                        friendProfile.getMemberId(),
                        friendProfile.getProfileImage(),
                        friendProfile.getName(),
                        "",
                        friendProfile.getActivities().getFirst().getGeneration(),
                        friendProfile.getActivities().getFirst().getPlaygroundPart().getPartName(),
                        friendRelationInfo.getPokeNum(),
                        friendRelationInfo.getRelationName(),
                        createMutualFriendNames(user.getId(), friendId),
                        false,
                        getIsAlreadyPoke(userId, friendId, userId),
                        getIsAnonymous(userId, friendId, userId),
                        friendRelationInfo.getAnonymousName()
                )
        );
    }

    private boolean getIsAlreadyPoke(Long pokerId, Long pokedId, Long userId) {
        return pokeHistoryService.getAllPokeHistoryByUsers(pokerId, pokedId).stream()
                .filter(pokeHistory -> pokeHistory.getPokerId().equals(userId))
                .anyMatch(pokeHistory -> !pokeHistory.getIsReply());
    }

    private boolean getIsAnonymous(Long pokerId, Long pokedId, Long userId) {
        return pokeHistoryService.getAllPokeHistoryByUsers(pokerId, pokedId).stream()
                .filter(pokeHistory -> pokeHistory.getPokedId().equals(userId))
                .max(Comparator.comparing(PokeHistoryInfo::getCreatedAt))
                .map(PokeHistoryInfo::getIsAnonymous).orElse(false);
    }

    private String createMutualFriendNames(Long userId, Long friendId) {
        List<String> mutualFriendNames = userService.getNamesByIds(friendService.getMutualFriendIds(userId, friendId));

        if (mutualFriendNames.isEmpty()) {
            return NEW_FRIEND_NO_MUTUAL;
        }
        if (mutualFriendNames.size() == 1) {
            return String.format(NEW_FRIEND_ONE_MUTUAL, mutualFriendNames.getFirst());
        } else {
            return String.format(NEW_FRIEND_MANY_MUTUAL, mutualFriendNames.getFirst(), mutualFriendNames.size() - 1);
        }
    }

    @Transactional(readOnly = true)
    public List<SimplePokeProfile> getTwoFriendByFriendship(User user, Friendship friendship) {
        val friendsOfFriendship = friendService.findAllFriendsByFriendship(
                user.getId(), friendship.getLowerLimit(), friendship.getUpperLimit());

        return friendsOfFriendship.stream()
                .map(friend -> {
                    List<PokeHistoryInfo> allOfPokeFromTo = pokeHistoryService.getAllOfPokeBetween(friend.getUserId(),
                            friend.getFriendUserId());
                    return allOfPokeFromTo.stream()
                            .map(poke -> getPokeHistoryProfile(user, friend.getFriendUserId(), poke.getId()))
                            .findFirst().get();
                })
                .limit(2)
                .toList();
    }

    @Transactional(readOnly = true)
    public int getFriendSizeByFriendship(Long userId, Friendship friendship) {
        return friendService.findAllFriendsByFriendship(
                userId, friendship.getLowerLimit(), friendship.getUpperLimit()).size();
    }

    @Transactional(readOnly = true)
    public EachRelationFriendList getAllFriendByFriendship(User user, Friendship friendship, Pageable pageable) {
        val friends = friendService.findAllFriendsByFriendship(
                user.getId(), friendship.getLowerLimit(), friendship.getUpperLimit(), pageable);
        List<SimplePokeProfile> allOfPokeWithFriends = friends.getContent().stream()
                .filter(friend -> userService.isUserExist(friend.getFriendUserId()))
                .map(friend -> {
                    List<PokeHistoryInfo> allOfPokeFromTo = pokeHistoryService.getAllOfPokeBetween(
                            friend.getUserId(),
                            friend.getFriendUserId());
                    return allOfPokeFromTo.stream()
                            .map(poke -> getPokeHistoryProfile(user, friend.getFriendUserId(), poke.getId()))
                            .findFirst().get();
                }).toList();
        val totalSize = friendService.findAllFriendSizeByFriendship(
                user.getId(), friendship.getLowerLimit(), friendship.getUpperLimit());
        val totalPageSize = totalSize / pageable.getPageSize();
        return EachRelationFriendList.builder()
                .friendList(allOfPokeWithFriends)
                .totalSize(totalSize)
                .totalPageSize(totalPageSize)
                .pageSize(pageable.getPageSize())
                .pageNum(friends.getNumber())
                .build();
    }

    public SimplePokeProfile getPokeHistoryProfile(User user, Long friendId, Long pokeId) {
        PokeInfo.PokeDetail pokeDetail = pokeService.getPokeDetail(pokeId);
        PokeInfo.PokedUserInfo friendUserInfo = getFriendUserInfo(user, friendId);

        return SimplePokeProfile.from(
                friendUserInfo,
                pokeDetail,
                getIsAlreadyPoke(pokeDetail.getPokerId(), pokeDetail.getPokedId(), user.getId()),
                getIsAnonymous(pokeDetail.getPokerId(), pokeDetail.getPokedId(), user.getId())
        );
    }

    private PokeInfo.PokedUserInfo getFriendUserInfo(User user, Long friendUserId) {
        val pokedUser = userService.getUserProfileOrElseThrow(friendUserId);
        val pokedUserProfile = userService.getUserProfilesByPlaygroundIds(List.of(pokedUser.getPlaygroundId())).get(0);
        val pokedUserPlaygroundProfile = playgroundAuthService.getPlaygroundMemberProfiles(
                user.getPlaygroundToken(), List.of(pokedUserProfile.getPlaygroundId())).get(0);
        val latestActivity = pokedUserPlaygroundProfile.getLatestActivity();
        val mutualFriendNames = userService.getNamesByIds(friendService.getMutualFriendIds(user.getId(), friendUserId));
        val relationInfo = friendService.getRelationInfo(user.getId(), friendUserId);
        return PokeInfo.PokedUserInfo.builder()
                .userId(pokedUserProfile.getUserId())
                .playgroundId(pokedUserPlaygroundProfile.getMemberId())
                .name(pokedUserPlaygroundProfile.getName())
                .profileImage(pokedUserPlaygroundProfile.getProfileImage())
                .generation(latestActivity.getGeneration())
                .part(latestActivity.getPlaygroundPart().getPartName())
                .relation(relationInfo)
                .mutualFriendNames(mutualFriendNames)
                .build();
    }

    public RecommendedFriendsRequest getRecommendedFriendsByTypeList(
            List<FriendRecommendType> typeList, int size, User user){
        return friendRecommender.recommendFriendsByTypeList(typeList, size, user);
    }

    public boolean getIsNewUser(Long userId) {
        return friendService.getIsNewUser(userId);
    }

    public Long getUserPokeCount(Long userId) {
        return pokeService.getUserPokeCount(userId);
    }
}