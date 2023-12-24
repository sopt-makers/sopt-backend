package org.sopt.app.facade;

import java.util.*;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.val;
import org.sopt.app.application.auth.PlaygroundAuthInfo;
import org.sopt.app.application.auth.PlaygroundAuthService;
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
        val messages = pokeMessageService.pickRandomMessageByTypeOf(type);
        return messages.stream()
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
                    playgroundProfile.getId(),
                    playgroundProfile.getProfileImage(),
                    playgroundProfile.getName(),
                    "",
                    Integer.parseInt(generation),
                    part,
                    0,
                    Friendship.NON_FRIEND.getFriendshipName(),
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
        val friendsUserIds = friendService.findAllFriendIdsByUserIdRandomly(user.getId(), 2);
        return friendsUserIds.stream().map(
            friendsUserId -> {
                val friendUser = userService.getUserProfile(friendsUserId);
                val friendProfile = playgroundAuthService.getPlaygroundMemberProfiles(user.getPlaygroundToken() ,List.of(
                    friendUser.getPlaygroundId())).get(0);
                val recommendUserProfiles = userService.findRandomFriendsOfFriends(user.getId(), friendsUserId, 2);
                val playgroundProfiles = playgroundAuthService.getPlaygroundMemberProfiles(user.getPlaygroundToken(), recommendUserProfiles.stream().map(UserProfile::getPlaygroundId).toList());
                val simpleProfiles = makeDummySimplePokeProfile(recommendUserProfiles, playgroundProfiles);
                return PokeResponse.Friend.of(
                    friendsUserId,
                    friendProfile.getId(),
                    friendProfile.getName(),
                    friendProfile.getProfileImage(),
                    simpleProfiles
                );
            }
        ).toList();
    }

    @Transactional(readOnly = true)
    public List<SimplePokeProfile> getAllPokeMeHistory(User user) {
        List<PokeHistory> pokedHistories = pokeHistoryService.getAllPokedHistoryOrderByMostRecent(user.getId());
        return pokedHistories.stream()
                .filter(pokeHistory -> !pokeHistory.getIsReply())
                .map(pokeHistory ->
                    getPokeHistoryProfile(user, pokeHistory.getPokerId(),pokeHistory.getId()))
                .distinct()
                .toList();
    }

    @Transactional(readOnly = true)
    public PokeToMeHistoryList getAllPokeMeHistory(User user, Pageable pageable) {
        Page<PokeHistory> pokedHistories = pokeHistoryService.getAllPokedHistoryOrderByMostRecent(user.getId(), pageable);
        List<SimplePokeProfile> pokeToMeHistories = pokedHistories.stream()
                .filter(pokeHistory -> !pokeHistory.getIsReply())
                .map(pokeHistory -> getPokeHistoryProfile(user, pokeHistory.getPokerId(), pokeHistory.getId()))
                .distinct()
                .toList();
        return PokeToMeHistoryList.of(
                pokeToMeHistories,
                pageable.getPageSize(),
                pokedHistories.getNumber()
        );
    }


    @Transactional
    public PokeHistory pokeFriend(Long pokerUserId, Long pokedUserId, String pokeMessage) {
        pokeHistoryService.checkUserOverDailyPokeLimit(pokerUserId);
        PokeHistory newPoke = pokeService.poke(pokerUserId, pokedUserId, pokeMessage);

        applyFriendship(pokerUserId, pokedUserId);
        return newPoke;
    }
    private void applyFriendship(Long pokerUserId, Long pokedUserId) {
        // 친구 관계 확인
        boolean friendEachOther = friendService.isFriendEachOther(pokerUserId, pokedUserId);
        if (friendEachOther) {
            friendService.applyPokeCount(pokerUserId, pokedUserId);
            return;
        }
        boolean userNotPokeBefore = pokeHistoryService.getAllOfPokeBetween(
            pokerUserId, pokedUserId).isEmpty();
        boolean friendNotPokeBefore = pokeHistoryService.getAllOfPokeBetween(
            pokedUserId, pokerUserId).isEmpty();
        if (!userNotPokeBefore && !friendNotPokeBefore) {
            friendService.createRelation(pokerUserId, pokedUserId);
        }
    }


    @Transactional(readOnly = true)
    public List<SimplePokeProfile> getFriend(User user) {
        val pokeUserIds = pokeHistoryService.getPokeFriendIds(user.getId());
        val friendId = friendService.getPokeFriendIdRandomly(
            user.getId(),
            pokeUserIds);

        val friendUserProfile = userService.getUserProfileByUserId(friendId);
        val friendPlaygroundIds = friendUserProfile.stream().map(UserProfile::getPlaygroundId).toList();
        val friendProfile = playgroundAuthService.getPlaygroundMemberProfiles(user.getPlaygroundToken(), friendPlaygroundIds);
        val friendRelationInfo = friendService.getRelationInfo(user.getId(), friendId.get(0));
        val mutualFriendNames = friendService.getMutualFriendIds(user.getId(), friendId.get(0)).stream()
                .map(id -> {
                    UserInfo.UserProfile friendProfile1 = userService.getUserProfile(id);
                    return friendProfile1.getName();
                })
                .toList();

        val pokeHistory = pokeHistoryService.getAllOfPokeBetween(user.getId(), friendId.get(0)).get(0);
        val isAlreadyPoke = pokeHistory.getPokerId().equals(user.getId());

        return List.of(
            SimplePokeProfile.of(
                friendUserProfile.get(0).getUserId(),
                friendProfile.get(0).getId(),
                friendProfile.get(0).getProfileImage(),
                friendProfile.get(0).getName(),
                "",
                Integer.parseInt(friendProfile.get(0).getActivities().get(0).getGeneration()),
                friendProfile.get(0).getActivities().get(0).getPart(),
                friendRelationInfo.getPokeCount(),
                friendRelationInfo.getRelationName(),
                mutualFriendNames,
                false,
                isAlreadyPoke
            )
        );
    }

    @Transactional(readOnly = true)
    public List<SimplePokeProfile> getTwoFriendByFriendship(User user, Friendship friendship) {
        val friendsOfFriendship = friendService.findAllFriendsByFriendship(
            user.getId(), friendship.getLowerLimit(), friendship.getUpperLimit());
        return friendsOfFriendship.stream()
                .map(friend -> {
                    List<PokeHistory> allOfPokeFromTo = pokeHistoryService.getAllOfPokeBetween(friend.getUserId(), friend.getFriendUserId());
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
                .map(friend -> {
                    List<PokeHistory> allOfPokeFromTo = pokeHistoryService.getAllOfPokeBetween(friend.getUserId(), friend.getFriendUserId());
                    return allOfPokeFromTo.stream()
                            .map(poke -> getPokeHistoryProfile(user, friend.getFriendUserId(), poke.getId()))
                            .findFirst().get();
                }).toList();
        val totalSize = friendService.findAllFriendSizeByFriendship(
                user.getId(), friendship.getLowerLimit(), friendship.getUpperLimit());
        return EachRelationFriendList.of(
                allOfPokeWithFriends,
                totalSize,
                // TODO: 여기서 필요한 PageSize의 값이 조회 결과 리스트의 Elements Size 인지,
                //  이후 API 재호출 시 사용할 RequestParam 값을 위해 넣어주는 건지 논의
                pageable.getPageSize(),
                friends.getNumber()
        );
    }

    @Transactional(readOnly = true)
    public SimplePokeProfile getPokeHistoryProfile(User user, Long friendId, Long pokeId) {
        // 나에 대해 찌른 내역을 반환
        PokeInfo.PokeDetail pokeDetail = getPokeInfo(pokeId);

        val isAlreadyReply = pokeDetail.getPokerId().equals(user.getId());

        PokeInfo.PokedUserInfo friendUserInfo = getFriendUserInfo(
                user, friendId);

        return SimplePokeProfile.of(
                friendUserInfo.getUserId(),
                friendUserInfo.getPlaygroundId(),
                friendUserInfo.getProfileImage(),
                friendUserInfo.getName(),
                pokeDetail.getMessage(),
                friendUserInfo.getGeneration(),
                friendUserInfo.getPart(),
                friendUserInfo.getRelation().getPokeCount(),
                friendUserInfo.getRelation().getRelationName(),
                friendUserInfo.getMutualFriendNames(),
                friendUserInfo.getRelation().getPokeCount() == 0,
                isAlreadyReply
        );
    }


    private PokeInfo.PokedUserInfo getFriendUserInfo(User user, Long friendUserId) {
        val pokedUser = userService.getUserProfile(friendUserId);
        val pokedUserProfile =  userService.getUserProfilesByPlaygroundIds(List.of(pokedUser.getPlaygroundId())).get(0);
        val pokedUserPlaygroundProfile = (PlaygroundAuthInfo.MemberProfile) playgroundAuthService.getPlaygroundMemberProfiles(user.getPlaygroundToken(), List.of(pokedUserProfile.getPlaygroundId())).get(0);
        val latestActivity = pokedUserPlaygroundProfile.getLatestActivity();
        val mutualFriendNames = friendService.getMutualFriendIds(user.getId(), friendUserId).stream()
                .map(id -> {
                    UserInfo.UserProfile friendProfile = userService.getUserProfile(id);
                    return friendProfile.getName();
                })
                .toList();
        if (friendService.isFriendEachOther(user.getId(), friendUserId)) {
            val relationInfo = friendService.getRelationInfo(user.getId(), friendUserId);
            return PokeInfo.PokedUserInfo.builder()
                    .userId(pokedUserProfile.getUserId())
                    .playgroundId(pokedUserPlaygroundProfile.getId())
                    .name(pokedUserPlaygroundProfile.getName())
                    .profileImage(pokedUserPlaygroundProfile.getProfileImage())
                    .generation(Integer.parseInt(latestActivity.getGeneration()))
                    .part(latestActivity.getPart())
                    .relation(relationInfo)
                    .mutualFriendNames(mutualFriendNames)
                    .build();
        }
        return PokeInfo.PokedUserInfo.builder()
                .userId(pokedUserProfile.getUserId())
                .playgroundId(pokedUserPlaygroundProfile.getId())
                .name(pokedUserPlaygroundProfile.getName())
                .profileImage(pokedUserPlaygroundProfile.getProfileImage())
                .generation(Integer.parseInt(latestActivity.getGeneration()))
                .part(latestActivity.getPart())
                .relation(
                        PokeInfo.Relationship.builder()
                                .pokeCount(0)
                                .relationName("")
                                .build()
                )
                .mutualFriendNames(mutualFriendNames)
                .build();
    }

    private PokeInfo.PokeDetail getPokeInfo(Long pokeHistoryId) {
        return pokeService.getPokeDetail(pokeHistoryId);
    }

}
