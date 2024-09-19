package org.sopt.app.facade;

import static org.sopt.app.application.poke.PokeInfo.NEW_FRIEND_MANY_MUTUAL;
import static org.sopt.app.application.poke.PokeInfo.NEW_FRIEND_NO_MUTUAL;
import static org.sopt.app.application.poke.PokeInfo.NEW_FRIEND_ONE_MUTUAL;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Random;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.val;
import org.sopt.app.application.auth.dto.PlaygroundProfileInfo.ActivityCardinalInfo;
import org.sopt.app.application.auth.dto.PlaygroundProfileInfo.OwnPlaygroundProfile;
import org.sopt.app.application.auth.dto.PlaygroundProfileInfo.PlaygroundProfile;
import org.sopt.app.application.playground.PlaygroundAuthService;
import org.sopt.app.application.poke.FriendService;
import org.sopt.app.application.poke.PokeHistoryService;
import org.sopt.app.application.poke.PokeInfo;
import org.sopt.app.application.poke.PokeInfo.PokeHistoryInfo;
import org.sopt.app.application.poke.PokeMessageService;
import org.sopt.app.application.poke.PokeService;
import org.sopt.app.application.user.UserInfo.UserProfile;
import org.sopt.app.application.user.UserService;
import org.sopt.app.common.exception.BadRequestException;
import org.sopt.app.common.response.ErrorCode;
import org.sopt.app.domain.entity.poke.PokeHistory;
import org.sopt.app.domain.entity.User;
import org.sopt.app.domain.enums.FriendRecommendType;
import org.sopt.app.domain.enums.Friendship;
import org.sopt.app.presentation.poke.PokeResponse;
import org.sopt.app.presentation.poke.PokeResponse.EachRelationFriendList;
import org.sopt.app.presentation.poke.PokeResponse.PokeToMeHistoryList;
import org.sopt.app.presentation.poke.PokeResponse.RecommendedFriendsByAllType;
import org.sopt.app.presentation.poke.PokeResponse.RecommendedFriendsByType;
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
        val fixedMessage = pokeMessageService.getFixedMessage();
        messages.add(fixedMessage);

        return messages.stream().map(messagesDetail ->
                PokeResponse.PokeMessage.of(messagesDetail.getId(), messagesDetail.getContent())
        ).toList();
    }

    public String getPokingMessageHeader(String type) {
        return pokeMessageService.getMessagesHeaderComment(type);
    }

    @Transactional(readOnly = true)
    public List<SimplePokeProfile> getRecommendUserForNew(String playgroundToken, Long userPlaygroundId,
            Long userId
    ) {
        val playgroundUserIds = playgroundAuthService.getPlayGroundUserIds(playgroundToken);
        val notFriendUserPlaygroundIds = userService.getUserProfilesByPlaygroundIds(playgroundUserIds.userIds())
                .stream()
                .filter(userProfile -> !userId.equals(userProfile.getUserId()) && !friendService.isFriendEachOther(
                        userId, userProfile.getUserId()))
                .map(UserProfile::getPlaygroundId)
                .collect(Collectors.toList());
        final int RECOMMEND_USER_NUM_FOR_NEW = 6;
        val recommendUserIds = pickRandomUserIds(notFriendUserPlaygroundIds, userPlaygroundId,
                RECOMMEND_USER_NUM_FOR_NEW
        );
        val playgroundProfiles = playgroundAuthService.getPlaygroundMemberProfiles(playgroundToken, recommendUserIds);
        val userProfiles = userService.getUserProfilesByPlaygroundIds(recommendUserIds);
        val pokeHistories = pokeHistoryService.getAllPokeHistoryMap(userId);
        return makeRandomSimplePokeProfile(userProfiles, playgroundProfiles, pokeHistories, userId);
    }

    private List<SimplePokeProfile> makeRandomSimplePokeProfile(
            List<UserProfile> userProfiles,
            List<PlaygroundProfile> playgroundProfiles,
            Map<Long, Boolean> pokeHistories,
            Long userId
    ) {
        return userProfiles.stream().map(
                userProfile -> {
                    val isAlreadyPoke = Objects.nonNull(pokeHistories.get(userProfile.getUserId()));
                    val pokeCount = friendService.getRelationInfo(userId, userProfile.getUserId()).getPokeNum();
                    val playgroundProfile = playgroundProfiles.stream()
                            .filter(profile -> profile.getMemberId().equals(userProfile.getPlaygroundId()))
                            .findFirst()
                            .orElseThrow(() -> new RuntimeException("플레이그라운드 프로필이 없습니다."));
                    val generation = playgroundProfile.getActivities().get(0).getGeneration();
                    val part = playgroundProfile.getActivities().get(0).getPart();

                    return SimplePokeProfile.of(
                            userProfile.getUserId(),
                            playgroundProfile.getMemberId(),
                            playgroundProfile.getProfileImage(),
                            playgroundProfile.getName(),
                            "",
                            generation,
                            part,
                            pokeCount,
                            Friendship.NON_FRIEND.getFriendshipName(),
                            NEW_FRIEND_NO_MUTUAL,
                            true,
                            isAlreadyPoke,
                            false,
                            ""
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
        return copiedList.stream().limit(limitNum).toList();
    }

    @Transactional(readOnly = true)
    public List<PokeResponse.Friend> getRecommendFriendsOfUsersFriend(User user) {
        val randomFriendsUserIds = friendService.findAllFriendIdsByUserIdRandomly(user.getId(), 2);
        val excludedUserIds = this.getExcludedUserIds(user.getId());

        return randomFriendsUserIds.stream().map(
                friendsUserId -> {
                    val friendUser = userService.getUserProfileOrElseThrow(friendsUserId);
                    val friendProfile = playgroundAuthService.getPlaygroundMemberProfiles(
                            user.getPlaygroundToken(), List.of(friendUser.getPlaygroundId())
                    ).get(0);

                    val randomFriendsIds = friendService.findAllFriendIdsByUserIdRandomlyExcludeUserId(friendsUserId,
                            excludedUserIds, 2);

                    if (randomFriendsIds.isEmpty()) {
                        return PokeResponse.Friend.of(
                                friendsUserId,
                                friendProfile.getMemberId(),
                                friendProfile.getName(),
                                friendProfile.getProfileImage(),
                                List.of()
                        );
                    }

                    val randomFriendsOfFriends = userService.getUserProfilesByUserIds(randomFriendsIds);

                    val pokeHistories = pokeHistoryService.getAllPokeHistoryMap(user.getId());
                    val playgroundProfiles = playgroundAuthService.getPlaygroundMemberProfiles(
                            user.getPlaygroundToken(),
                            randomFriendsOfFriends.stream().map(UserProfile::getPlaygroundId).toList());
                    val simpleProfiles = makeRandomSimplePokeProfile(
                            randomFriendsOfFriends,
                            playgroundProfiles,
                            pokeHistories,
                            user.getId()
                    );

                    return PokeResponse.Friend.of(
                            friendsUserId,
                            friendProfile.getMemberId(),
                            friendProfile.getName(),
                            friendProfile.getProfileImage(),
                            simpleProfiles
                    );
                }
        ).toList();
    }

    private List<Long> getExcludedUserIds(Long userId) {
        List<Long> hasPokeMeBeforeUserIds = pokeHistoryService.getPokeFriendIds(userId);
        List<Long> friendUserIds = friendService.findAllFriendIdsByUserId(userId);

        List<Long> excludedUserIds = new ArrayList<>(hasPokeMeBeforeUserIds);
        excludedUserIds.addAll(friendUserIds);
        excludedUserIds.add(userId);

        return excludedUserIds;
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
        return PokeToMeHistoryList.of(
                pokeToMeHistories,
                totalPageSize,
                pageable.getPageSize(),
                pokedHistories.getNumber()
        );
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
                        friendProfile.getActivities().get(0).getGeneration(),
                        friendProfile.getActivities().get(0).getPart(),
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
            return String.format(NEW_FRIEND_ONE_MUTUAL, mutualFriendNames.get(0));
        } else {
            return String.format(NEW_FRIEND_MANY_MUTUAL, mutualFriendNames.get(0), mutualFriendNames.size() - 1);
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
        return EachRelationFriendList.of(
                allOfPokeWithFriends,
                totalSize,
                totalPageSize,
                pageable.getPageSize(),
                friends.getNumber()
        );
    }

    public SimplePokeProfile getPokeHistoryProfile(User user, Long friendId, Long pokeId) {
        PokeInfo.PokeDetail pokeDetail = getPokeInfo(pokeId);
        PokeInfo.PokedUserInfo friendUserInfo = getFriendUserInfo(user, friendId);

        return SimplePokeProfile.from(
                friendUserInfo,
                pokeDetail,
                getIsAlreadyPoke(pokeDetail.getPokerId(), pokeDetail.getPokedId(), user.getId()),
                getIsAnonymous(pokeDetail.getPokerId(), pokeDetail.getPokedId(), user.getId())
        );
    }

    public RecommendedFriendsByAllType getRecommendedFriendsByTypeList(List<FriendRecommendType> typeList, int size,
            User user) {
        List<RecommendedFriendsByType> recommendedFriendsByTypeList = new ArrayList<>();

        OwnPlaygroundProfile ownPlaygroundProfile = playgroundAuthService.getOwnPlaygroundProfile(
                user.getPlaygroundToken());
        Long latestGeneration = getLatestGenerationByActivityCardinalInfoList(ownPlaygroundProfile.getActivities());
        String mbti = ownPlaygroundProfile.getMbti();
        String university = ownPlaygroundProfile.getUniversity();

        for (FriendRecommendType type : typeList) {
            List<SimplePokeProfile> recommendedFriendProfiles;
            switch (type) {
                case ALL:
                    handleAllType(recommendedFriendsByTypeList, ownPlaygroundProfile, size, user);
                    return RecommendedFriendsByAllType.of(recommendedFriendsByTypeList);
                case GENERATION:
                    recommendedFriendProfiles = findRecommendedFriendsListByGeneration(user, size,
                            this.getAllGenerationByActivityCardinalInfoList(ownPlaygroundProfile.getActivities()),
                            playgroundAuthService::getPlaygroundIdsForSameGeneration);

                    if (!recommendedFriendProfiles.isEmpty()) {
                        recommendedFriendsByTypeList.add(getRecommendedFriendsByType(type, recommendedFriendProfiles));
                    }
                    break;
                case MBTI:
                    if (mbti != null) {
                        recommendedFriendProfiles = findRecommendedFriendsList(user, size, latestGeneration, mbti,
                                playgroundAuthService::getPlaygroundIdsForSameMbti);

                        if (!recommendedFriendProfiles.isEmpty()) {
                            recommendedFriendsByTypeList.add(
                                    getRecommendedFriendsByType(type, recommendedFriendProfiles));
                        }
                    }
                    break;
                case UNIVERSITY:
                    if (university != null) {
                        recommendedFriendProfiles = findRecommendedFriendsList(user, size, latestGeneration, university,
                                playgroundAuthService::getPlaygroundIdsForSameUniversity);

                        if (!recommendedFriendProfiles.isEmpty()) {
                            recommendedFriendsByTypeList.add(
                                    getRecommendedFriendsByType(type, recommendedFriendProfiles));
                        }
                    }
                    break;
                default:
                    throw new BadRequestException(ErrorCode.INVALID_FRIEND_RECOMMEND_TYPE.getMessage());
            }
        }
        return RecommendedFriendsByAllType.of(recommendedFriendsByTypeList);
    }


    private void handleAllType(List<RecommendedFriendsByType> recommendedFriendsByTypeList,
            OwnPlaygroundProfile ownPlaygroundProfile, int size, User user) {
        Long latestGeneration = getLatestGenerationByActivityCardinalInfoList(ownPlaygroundProfile.getActivities());
        String mbti = ownPlaygroundProfile.getMbti();
        String university = ownPlaygroundProfile.getUniversity();
        List<SimplePokeProfile> recommendedFriendProfiles;

        recommendedFriendProfiles = findRecommendedFriendsListByGeneration(user, size,
                this.getAllGenerationByActivityCardinalInfoList(ownPlaygroundProfile.getActivities()),
                playgroundAuthService::getPlaygroundIdsForSameGeneration);

        if (!recommendedFriendProfiles.isEmpty()) {
            recommendedFriendsByTypeList.add(
                    getRecommendedFriendsByType(FriendRecommendType.GENERATION, recommendedFriendProfiles));
        }

        if (mbti != null) {
            recommendedFriendProfiles = findRecommendedFriendsList(user, size, latestGeneration, mbti,
                    playgroundAuthService::getPlaygroundIdsForSameMbti);

            if (!recommendedFriendProfiles.isEmpty()) {
                recommendedFriendsByTypeList.add(
                        getRecommendedFriendsByType(FriendRecommendType.MBTI, recommendedFriendProfiles));
            }
        }

        if (university != null) {
            recommendedFriendProfiles = findRecommendedFriendsList(user, size, latestGeneration, university,
                    playgroundAuthService::getPlaygroundIdsForSameUniversity);

            if (!recommendedFriendProfiles.isEmpty()) {
                recommendedFriendsByTypeList.add(
                        getRecommendedFriendsByType(FriendRecommendType.UNIVERSITY, recommendedFriendProfiles));
            }
        }
    }

    private List<SimplePokeProfile> findRecommendedFriendsListByGeneration(User user, int size,
            List<Long> generationList, Function<List<Long>, List<Long>> fetchProfilesFunction) {
        List<Long> recommendedPlaygroundIds = fetchProfilesFunction.apply(generationList);
        return getRecommendedFriendsBySize(user, size, recommendedPlaygroundIds);
    }

    private List<SimplePokeProfile> getRecommendedFriendsBySize(User user, int size,
            List<Long> recommendedPlaygroundIds) {
        List<UserProfile> recommendedUserProfiles = userService.getUserProfilesByPlaygroundIds(
                recommendedPlaygroundIds);
        List<UserProfile> validatedUserProfiles = excludeProfileLinkedFriends(recommendedUserProfiles, user.getId());

        if (validatedUserProfiles.isEmpty()) {
            return Collections.emptyList();
        }
        return createNonFriendPokeProfile(
                selectRandomProfilesOfSize(validatedUserProfiles, size), user.getPlaygroundToken());
    }

    private <T> List<SimplePokeProfile> findRecommendedFriendsList(User user, int size, Long latestGeneration,
            T value, BiFunction<Long, T, List<Long>> fetchProfilesFunction) {
        List<Long> recommendedPlaygroundIds = fetchProfilesFunction.apply(latestGeneration, value);
        return getRecommendedFriendsBySize(user, size, recommendedPlaygroundIds);
    }

    private List<UserProfile> excludeProfileLinkedFriends(List<UserProfile> profiles, Long userId) {
        List<Long> userIdsToBeExcluded = this.getUserIdsToBeExcluded(userId);
        userIdsToBeExcluded.add(userId);

        return profiles.stream()
                .filter(profile -> !userIdsToBeExcluded.contains(profile.getUserId()))
                .collect(Collectors.toList());
    }

    private List<Long> getUserIdsToBeExcluded(Long userId) {
        List<Long> userIdsLinkedFriends = friendService.findUserIdsLinkedFriends(userId);
        userIdsLinkedFriends.add(userId);
        return userIdsLinkedFriends;
    }

    private List<UserProfile> selectRandomProfilesOfSize(List<UserProfile> profiles, int size) {
        Collections.shuffle(profiles, new Random());
        return profiles.stream().limit(size).toList();
    }

    private RecommendedFriendsByType getRecommendedFriendsByType(FriendRecommendType type,
            List<SimplePokeProfile> recommendedFriends) {

        return RecommendedFriendsByType.of(
                type,
                type.getRecommendTitle(),
                recommendedFriends
        );
    }

    private List<SimplePokeProfile> createNonFriendPokeProfile(List<UserProfile> userProfiles, String playgroundToken) {
        List<PlaygroundProfile> a = playgroundAuthService.getPlaygroundMemberProfiles(
                playgroundToken, userProfiles.stream().map(UserProfile::getPlaygroundId).toList());

        return a.stream().map(profile ->
                SimplePokeProfile.createNonFriendPokeProfile(
                        userProfiles.stream().filter(
                                        userProfile -> userProfile.getPlaygroundId().equals(profile.getMemberId()))
                                .findFirst()
                                .orElseThrow(() -> new BadRequestException(ErrorCode.USER_NOT_FOUND.getMessage()))
                                .getUserId(),
                        profile.getMemberId(),
                        profile.getProfileImage(),
                        profile.getName(),
                        profile.getLatestActivity().getGeneration(),
                        profile.getLatestActivity().getPart()
                )).toList();
    }

    private Long getLatestGenerationByActivityCardinalInfoList(List<ActivityCardinalInfo> activityCardinalInfoList) {
        return activityCardinalInfoList.stream()
                        .filter(ActivityCardinalInfo::isActualGeneration)
                        .max(Comparator.comparing(ActivityCardinalInfo::getGeneration))
                        .orElseThrow(
                                () -> new BadRequestException(ErrorCode.USER_GENERATION_INFO_NOT_FOUND.getMessage()))
                        .getGeneration();
    }

    private List<Long> getAllGenerationByActivityCardinalInfoList(
            List<ActivityCardinalInfo> activityCardinalInfoList) {
        return activityCardinalInfoList.stream()
                .filter(ActivityCardinalInfo::isActualGeneration)
                .map(ActivityCardinalInfo::getGeneration)
                .toList();
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
                .part(latestActivity.getPart())
                .relation(relationInfo)
                .mutualFriendNames(mutualFriendNames)
                .build();
    }

    private PokeInfo.PokeDetail getPokeInfo(Long pokeHistoryId) {
        return pokeService.getPokeDetail(pokeHistoryId);
    }

    public boolean getIsNewUser(Long userId) {
        return friendService.getIsNewUser(userId);
    }
}
