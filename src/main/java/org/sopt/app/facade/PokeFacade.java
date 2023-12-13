package org.sopt.app.facade;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.val;
import org.sopt.app.application.auth.PlaygroundAuthInfo;
import org.sopt.app.application.auth.PlaygroundAuthService;
import org.sopt.app.application.friend.FriendInfo;
import org.sopt.app.application.friend.FriendInfo.Friend;
import org.sopt.app.application.poke.*;
import org.sopt.app.application.user.UserInfo;
import org.sopt.app.application.user.UserInfo.UserProfile;
import org.sopt.app.application.user.UserService;
import org.sopt.app.domain.entity.PokeHistory;
import org.sopt.app.domain.entity.User;
import org.sopt.app.domain.enums.Friendship;
import org.sopt.app.presentation.poke.PokeResponse;
import org.sopt.app.presentation.poke.PokeResponse.EachRelationFriendList;
import org.sopt.app.presentation.poke.PokeResponse.PokeToMeHistoryList;
import org.sopt.app.presentation.poke.PokeResponse.SimplePokeProfile;
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
    private final PokeService pokeService;
    private final PokeHistoryService pokeHistoryService;
    private final PokeMessageService pokeMessageService;

    @Transactional(readOnly = true)
    public List<PokeResponse.PokeMessage> getPokingMessages(String type) {
        List<Long> targetMessageIds = pokeMessageService.pickRandomMessageIdsTypeOf(type);
        List<PokeInfo.PokeMessageDetail> messagesDetails = pokeMessageService.getMessagesDetail(targetMessageIds);
        return messagesDetails.stream()
                .map(messagesDetail -> PokeResponse.PokeMessage.of(
                        messagesDetail.getId(), messagesDetail.getContent()
                    )
                )
                .toList();
    }

    @Transactional(readOnly = true)
    public List<SimplePokeProfile> getRecommendUserForNew(String playgroundToken, Long userPlaygroundId) {
        val playgroundUserIds = playgroundAuthService.getPlayGroundUserIds(playgroundToken);
        int RECOMMEND_USER_NUM_FOR_NEW = 6;
        val recommendUserIds = pickRandomUserIds(playgroundUserIds.getUserIds(), userPlaygroundId,
            RECOMMEND_USER_NUM_FOR_NEW
        );
        val playgroundProfiles = playgroundAuthService.getPlaygroundMemberProfiles(playgroundToken, recommendUserIds);
        val userProfiles = userService.getUserProfilesByPlaygroundIds(recommendUserIds);
        return makeDummySimplePokeProfile(userProfiles, playgroundProfiles);
    }

    private List<SimplePokeProfile> makeDummySimplePokeProfile(List<UserProfile> userProfiles, List<PlaygroundAuthInfo.MemberProfile> playgroundProfiles) {
        return userProfiles.stream().map(
            userProfile -> {
                val playgroundProfile = playgroundProfiles.stream()
                    .filter(profile -> profile.getId().equals(userProfile.getPlaygroundId()))
                    .findFirst()
                    .orElseThrow(() -> new RuntimeException("플레이그라운드 프로필이 없습니다."));
                val generation = playgroundProfile.getActivities().get(0).getGeneration();
                val part = playgroundProfile.getActivities().get(0).getPart();

                return SimplePokeProfile.of(
                    userProfile.getUserId(),
                    playgroundProfile.getProfileImage(),
                    playgroundProfile.getName(),
                    "",
                    Activity.builder()
                        .generation(Integer.parseInt(generation))
                        .part(part)
                        .build(),
                    0,
                    "",
                    List.of(),
                    true,
                    false
                );
            }
        ).toList();
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
    public List<PokeResponse.Friend> getRecommendFriendsOfUsersFriend(User user) {
        val userId = user.getId();
        val friendIds = friendService.findAllFriendIdsByUserIdRandomly(userId, 2);
        return friendIds.stream().map(
            friendId -> {
                val friendProfile = playgroundAuthService.getPlaygroundMemberProfiles(user.getPlaygroundToken() ,List.of(friendId)).get(0);
                val recommendUserProfiles = userService.findRandomFriendsOfFriends(userId, friendId, 2);
                val playgroundProfiles = playgroundAuthService.getPlaygroundMemberProfiles(user.getPlaygroundToken(), recommendUserProfiles.stream().map(UserProfile::getPlaygroundId).toList());
                val simpleProfiles = makeDummySimplePokeProfile(recommendUserProfiles, playgroundProfiles);
                return PokeResponse.Friend.of(
                    friendId,
                    friendProfile.getName(),
                    friendProfile.getProfileImage(),
                    simpleProfiles
                );
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
    public PokeToMeHistoryList getAllUserIdsOfPokeMe(User user, Pageable pageable) {
        Page<PokeHistory> currentHistory = pokeHistoryService.getPokeFriendIdsInOrderByMostRecent(user.getId(), pageable);
        List<SimplePokeProfile> pokeToMeHistories = currentHistory.stream()
                .map(PokeHistory::getPokerId)
                .distinct()
                .map(id -> getPokeHistoryProfileWith(user, id))
                .toList();
        return PokeToMeHistoryList.of(
                pokeToMeHistories,
                pageable.getPageSize(),
                currentHistory.getNumber()
        );
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
    public List<SimplePokeProfile> getFriend(User user) {
        val pokedFriendIds = pokeHistoryService.getPokedFriendIds(user.getId());
        val pokeFriendIds = pokeHistoryService.getPokeFriendIds(user.getId());
        val friendId = friendService.getNotPokeFriendIdRandomly(
            user.getId(),
            pokedFriendIds, pokeFriendIds);
        val friendUserProfile = userService.getUserProfileByUserId(friendId);
        val friendPlaygroundIds = friendUserProfile.stream().map(UserProfile::getPlaygroundId).toList();
        val friendProfile = playgroundAuthService.getPlaygroundMemberProfiles(user.getPlaygroundToken(), friendPlaygroundIds);

        return makeDummySimplePokeProfile(friendUserProfile, friendProfile);
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
    public EachRelationFriendList getFriendByFriendship(User user, Friendship friendship, Pageable pageable) {
        val friends = friendService.findAllFriendsByFriendship(
                user.getId(), friendship.getLowerLimit(), friendship.getUpperLimit(), pageable);
        val eachFriendsHistory = friends.getContent().stream()
                .map(friend -> getPokeHistoryProfileWith(user, friend.getFriendUserId()))
                .toList();
        return EachRelationFriendList.of(
                eachFriendsHistory,
                // TODO: 여기서 필요한 PageSize의 값이 조회 결과 리스트의 Elements Size 인지,
                //  이후 API 재호출 시 사용할 RequestParam 값을 위해 넣어주는 건지 논의
                pageable.getPageSize(),
                friends.getNumber()
        );
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
