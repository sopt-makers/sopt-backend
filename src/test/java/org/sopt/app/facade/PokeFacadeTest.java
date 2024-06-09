package org.sopt.app.facade;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.sopt.app.application.auth.PlaygroundAuthInfo;
import org.sopt.app.application.auth.PlaygroundAuthInfo.ActiveUserIds;
import org.sopt.app.application.auth.PlaygroundAuthInfo.ActivityCardinalInfo;
import org.sopt.app.application.auth.PlaygroundAuthInfo.OwnPlaygroundProfile;
import org.sopt.app.application.auth.PlaygroundAuthInfo.PlaygroundActivity;
import org.sopt.app.application.auth.PlaygroundAuthInfo.PlaygroundProfile;
import org.sopt.app.application.auth.PlaygroundAuthInfo.PlaygroundProfileOfRecommendedFriend;
import org.sopt.app.application.auth.PlaygroundAuthService;
import org.sopt.app.application.poke.FriendService;
import org.sopt.app.application.poke.PokeHistoryService;
import org.sopt.app.application.poke.PokeInfo.PokeDetail;
import org.sopt.app.application.poke.PokeInfo.PokeHistoryInfo;
import org.sopt.app.application.poke.PokeInfo.Relationship;
import org.sopt.app.application.poke.PokeMessageService;
import org.sopt.app.application.poke.PokeService;
import org.sopt.app.application.user.UserInfo.UserProfile;
import org.sopt.app.application.user.UserService;
import org.sopt.app.domain.entity.Friend;
import org.sopt.app.domain.entity.PokeHistory;
import org.sopt.app.domain.entity.PokeMessage;
import org.sopt.app.domain.entity.User;
import org.sopt.app.domain.enums.FriendRecommendType;
import org.sopt.app.domain.enums.Friendship;
import org.sopt.app.domain.enums.PokeMessageType;
import org.sopt.app.presentation.poke.PokeResponse;
import org.sopt.app.presentation.poke.PokeResponse.EachRelationFriendList;
import org.sopt.app.presentation.poke.PokeResponse.PokeToMeHistoryList;
import org.sopt.app.presentation.poke.PokeResponse.RecommendedFriendsByAllType;
import org.sopt.app.presentation.poke.PokeResponse.SimplePokeProfile;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

@ExtendWith(MockitoExtension.class)
class PokeFacadeTest {

    private static final String MESSAGES_HEADER_FOR_POKE = "함께 보낼 메시지를 선택해주세요";
    private final Relationship relationship1 = Relationship.builder().pokeNum(1).build();
    private final Relationship relationship2 = Relationship.builder().pokeNum(3).build();
    private final PlaygroundAuthInfo.ActiveUserIds activeUserIds = new ActiveUserIds();
    private final User user = User.builder().id(1L).playgroundToken("token").build();
    private final UserProfile userProfile1 = UserProfile.builder().userId(1L).name("name1").playgroundId(1L).build();
    private final UserProfile userProfile2 = UserProfile.builder().userId(2L).name("name2").playgroundId(2L).build();
    private final UserProfile userProfile3 = UserProfile.builder().userId(3L).name("name3").playgroundId(3L).build();
    private final UserProfile userProfile4 = UserProfile.builder().userId(4L).name("name4").playgroundId(4L).build();
    private final List<UserProfile> userProfileList = List.of(userProfile2, userProfile3);
    private final List<UserProfile> userProfileListIncludingMe = List.of(userProfile1, userProfile2, userProfile3);
    private final ActivityCardinalInfo activityCardinalInfo = ActivityCardinalInfo.builder().cardinalInfo("34,서버")
            .build();
    private final List<PlaygroundProfile> playgroundProfileList = List.of(
            new PlaygroundProfile(2L, "name2", "image", List.of(activityCardinalInfo)),
            new PlaygroundProfile(3L, "name3", "image", List.of(activityCardinalInfo))
    );
    private final List<PlaygroundProfile> playgroundProfileListWithoutImage = List.of(
            new PlaygroundProfile(2L, "name2", "", List.of(activityCardinalInfo)),
            new PlaygroundProfile(3L, "name3", "", List.of(activityCardinalInfo))
    );
    private final List<Long> userIdListExcludeMe = List.of(2L, 3L);
    private final List<PokeResponse.Friend> friendList = List.of(
            PokeResponse.Friend.of(2L, 2L, "name2", "", List.of()),
            PokeResponse.Friend.of(3L, 3L, "name2", "", List.of())
    );
    private final PokeHistory pokeHistory2 = PokeHistory.builder().id(2L).pokedId(1L).pokerId(2L).isReply(false)
            .isAnonymous(false).build();
    private final PokeHistoryInfo pokeHistoryInfo2 = PokeHistoryInfo.builder().id(2L).pokedId(1L).pokerId(2L)
            .isReply(false)
            .isAnonymous(false).build();
    private final PokeHistoryInfo pokeHistoryInfo2PokedIsNotReply = PokeHistoryInfo.builder().id(3L).pokedId(2L)
            .pokerId(1L)
            .isReply(false).isAnonymous(false).build();
    private final PokeHistoryInfo pokeHistoryInfo2PokedIsReply = PokeHistoryInfo.builder().id(3L).pokedId(2L)
            .pokerId(1L)
            .isReply(true)
            .isAnonymous(false).build();
    private final PokeHistory pokeHistory3 = PokeHistory.builder().id(3L).pokedId(1L).pokerId(3L).isReply(true)
            .isAnonymous(false).build();
    private final PokeDetail pokeDetail2 = PokeDetail.builder().id(2L).pokedId(1L).pokerId(2L).message("message")
            .build();
    private final Friend friend2 = Friend.builder().id(2L).userId(1L).friendUserId(2L).pokeCount(1).anonymousName("")
            .build();
    private final PokeMessage fixedMessage = PokeMessage.builder().id(0L).content("콕").type(PokeMessageType.POKE_ALL)
            .build();

    @Mock
    private PokeMessageService pokeMessageService;
    @Mock
    private PlaygroundAuthService playgroundAuthService;
    @Mock
    private UserService userService;
    @Mock
    private FriendService friendService;
    @Mock
    private PokeHistoryService pokeHistoryService;
    @Mock
    private PokeService pokeService;
    @InjectMocks
    private PokeFacade pokeFacade;

    @BeforeEach
    public void beforeTest() {
        activeUserIds.setUserIds(List.of(1L, 2L, 3L));
    }

    @ParameterizedTest
    @ValueSource(strings = {"pokeSomeone", "pokeFriend", "replyNew"})
    @DisplayName("SUCCESS_찌르기 메세지 조회")
    void SUCCESS_getPokingMessage(String type) {
        PokeMessage pokeMessage = PokeMessage.builder().id(1L).content("content").build();
        ArrayList<PokeMessage> pokeMessageList = new ArrayList<>();
        pokeMessageList.add(pokeMessage);
        PokeResponse.PokeMessage pokeMessageResponse = PokeResponse.PokeMessage.of(1L, "content");
        PokeResponse.PokeMessage fixedMessageResponse = PokeResponse.PokeMessage.of(fixedMessage.getId(),
                fixedMessage.getContent());
        List<PokeResponse.PokeMessage> pokeMessageListResponse = List.of(pokeMessageResponse, fixedMessageResponse);

        when(pokeMessageService.pickRandomMessageByTypeOf(any())).thenReturn(pokeMessageList);
        when(pokeMessageService.getFixedMessage()).thenReturn(fixedMessage);

        List<PokeResponse.PokeMessage> result = pokeFacade.getPokingMessages(type);
        assertEquals(pokeMessageListResponse.size(), result.size());
        assertEquals(pokeMessageListResponse.get(0).getMessageId(), result.get(0).getMessageId());
        assertEquals(pokeMessageListResponse.get(0).getContent(), result.get(0).getContent());
    }

    @ParameterizedTest
    @ValueSource(strings = {"pokeSomeone", "pokeFriend", "replyNew"})
    @DisplayName("SUCCESS_찌르기 메세지 헤더 조회")
    void SUCCESS_getPokingMessageHeader(String type) {
        when(pokeMessageService.getMessagesHeaderComment(any())).thenReturn(MESSAGES_HEADER_FOR_POKE);

        String pokingMessageHeader = pokeFacade.getPokingMessageHeader(type);
        assertEquals(MESSAGES_HEADER_FOR_POKE, pokingMessageHeader);
    }

    @Test
    @DisplayName("SUCCESS_신규 유저 추천 조회")
    void SUCCESS_getRecommendUserForNew() {
        PlaygroundProfile playgroundProfile = new PlaygroundProfile(2L, "name2", "image",
                List.of(activityCardinalInfo));
        List<PlaygroundProfile> playgroundProfileListForNew = List.of(playgroundProfile);
        List<SimplePokeProfile> simplePokeProfileListForNew = List.of(
                SimplePokeProfile.of(2L, 2L, "image", "name2", "", 34, "서버", 1,
                        "새로운 친구", "새로운 친구", true, false, false, ""));

        when(playgroundAuthService.getPlayGroundUserIds("token")).thenReturn(activeUserIds);
        when(userService.getUserProfilesByPlaygroundIds(activeUserIds.getUserIds())).thenReturn(
                userProfileListIncludingMe);
        when(friendService.isFriendEachOther(1L, 2L)).thenReturn(false);
        when(friendService.isFriendEachOther(1L, 3L)).thenReturn(true);
        when(playgroundAuthService.getPlaygroundMemberProfiles(any(), any())).thenReturn(playgroundProfileListForNew);
        when(userService.getUserProfilesByPlaygroundIds(List.of(2L))).thenReturn(List.of(userProfile2));
        when(pokeHistoryService.getAllPokeHistoryMap(any())).thenReturn(new HashMap<>());
        when(friendService.getRelationInfo(any(), any())).thenReturn(relationship1);

        List<SimplePokeProfile> result = pokeFacade.getRecommendUserForNew("token", 1L, 1L);
        assertEquals(simplePokeProfileListForNew, result);
    }

    @Test
    @DisplayName("FAIL_추천 로직 플그 프로필 조회 불가 시 BadRequestException 발생")
    void FAIL_getRecommendUserForNewBadRequest() {
        when(playgroundAuthService.getPlayGroundUserIds(any())).thenReturn(activeUserIds);
        when(playgroundAuthService.getPlaygroundMemberProfiles(any(), any())).thenReturn(List.of());
        when(userService.getUserProfilesByPlaygroundIds(any())).thenReturn(userProfileList);
        when(pokeHistoryService.getAllPokeHistoryMap(any())).thenReturn(new HashMap<>());
        when(friendService.getRelationInfo(any(), any())).thenReturn(relationship1);

        assertThrows(RuntimeException.class, () ->
                pokeFacade.getRecommendUserForNew("token", 1L, 1L)
        );
    }

    @Test
    @DisplayName("SUCCESS_친구의 친구 있을 때 추천 조회")
    void SUCCESS_getRecommendFriendsOfUsersFriend() {
        when(friendService.findAllFriendIdsByUserIdRandomly(1L, 2)).thenReturn(List.of(2L, 3L));
        when(pokeHistoryService.getPokeFriendIds(1L)).thenReturn(List.of(4L));
        when(friendService.findAllFriendIdsByUserId(1L)).thenReturn(List.of(5L));
        when(userService.getUserProfile(2L)).thenReturn(userProfile2);
        when(userService.getUserProfile(3L)).thenReturn(userProfile3);
        when(playgroundAuthService.getPlaygroundMemberProfiles("token", List.of(2L))).thenReturn(playgroundProfileList);
        when(playgroundAuthService.getPlaygroundMemberProfiles("token", List.of(3L))).thenReturn(
                playgroundProfileListWithoutImage);
        when(playgroundAuthService.getPlaygroundMemberProfiles("token", List.of(2L, 3L))).thenReturn(
                playgroundProfileListWithoutImage);
        when(friendService.findAllFriendIdsByUserIdRandomlyExcludeUserId(any(), any(), anyInt())).thenReturn(
                userIdListExcludeMe);
        when(userService.getUserProfilesByUserIds(userIdListExcludeMe)).thenReturn(userProfileList);
        when(pokeHistoryService.getAllPokeHistoryMap(any())).thenReturn(new HashMap<>());
        when(friendService.getRelationInfo(any(), any())).thenReturn(relationship1);

        List<PokeResponse.Friend> result = pokeFacade.getRecommendFriendsOfUsersFriend(user);
        assertEquals(friendList.size(), result.size());
        assertEquals(friendList.get(0).getFriendId(), result.get(0).getFriendId());
        assertEquals(friendList.get(1).getFriendId(), result.get(1).getFriendId());
    }

    @Test
    @DisplayName("SUCCESS_친구의 친구 없을 때 추천 조회")
    void SUCCESS_getRecommendFriendsOfUsersFriendEmpty() {
        when(friendService.findAllFriendIdsByUserIdRandomly(1L, 2)).thenReturn(List.of(2L, 3L));
        when(pokeHistoryService.getPokeFriendIds(1L)).thenReturn(List.of(4L));
        when(friendService.findAllFriendIdsByUserId(1L)).thenReturn(List.of(5L));
        when(userService.getUserProfile(2L)).thenReturn(userProfile2);
        when(userService.getUserProfile(3L)).thenReturn(userProfile3);
        when(playgroundAuthService.getPlaygroundMemberProfiles("token", List.of(2L))).thenReturn(playgroundProfileList);
        when(playgroundAuthService.getPlaygroundMemberProfiles("token", List.of(3L))).thenReturn(
                playgroundProfileListWithoutImage);
        when(friendService.findAllFriendIdsByUserIdRandomlyExcludeUserId(any(), any(), anyInt())).thenReturn(
                List.of());

        List<PokeResponse.Friend> result = pokeFacade.getRecommendFriendsOfUsersFriend(user);
        assertEquals(friendList.size(), result.size());
        assertEquals(friendList.get(0).getFriendId(), result.get(0).getFriendId());
        assertEquals(friendList.get(1).getFriendId(), result.get(1).getFriendId());
    }

    @Test
    @DisplayName("SUCCESS_단일 누가 나를 찔렀어요 답장 X 조회")
    void SUCCESS_getMostRecentPokeMeHistoryIsNotReply() {
        SimplePokeProfile simplePokeProfile = SimplePokeProfile.of(2L, 2L, "image", "name2",
                "message", 34, "서버", 1, null, "name3의 친구", true, false, false, "");

        when(pokeHistoryService.getPokeMeUserIds(1L)).thenReturn(List.of(2L));
        when(pokeHistoryService.getAllLatestPokeHistoryFromTo(2L, 1L)).thenReturn(List.of(pokeHistory2));
        when(pokeService.getPokeDetail(pokeHistory2.getId())).thenReturn(pokeDetail2);
        when(userService.getUserProfile(2L)).thenReturn(userProfile2);
        when(playgroundAuthService.getPlaygroundMemberProfiles("token", List.of(2L))).thenReturn(playgroundProfileList);
        when(friendService.getMutualFriendIds(1L, 2L)).thenReturn(List.of(3L));
        when(userService.getUserProfile(3L)).thenReturn(userProfile3);
        when(friendService.getRelationInfo(1L, 2L)).thenReturn(relationship1);
        when(pokeHistoryService.getAllOfPokeBetween(2L, 1L)).thenReturn(List.of(pokeHistoryInfo2));
        when(userService.getUserProfilesByPlaygroundIds(anyList())).thenReturn(userProfileList);

        SimplePokeProfile result = pokeFacade.getMostRecentPokeMeHistory(user);
        assertEquals(simplePokeProfile, result);
    }

    @Test
    @DisplayName("SUCCESS_단일 누가 나를 찔렀어요 답장 O 조회")
    void SUCCESS_getMostRecentPokeMeHistoryIsReply() {
        when(pokeHistoryService.getPokeMeUserIds(1L)).thenReturn(List.of(3L));
        when(pokeHistoryService.getAllLatestPokeHistoryFromTo(3L, 1L)).thenReturn(List.of(pokeHistory3));

        SimplePokeProfile result = pokeFacade.getMostRecentPokeMeHistory(user);
        assertNull(result);
    }

    @Test
    @DisplayName("SUCCESS_리스트 누가 나를 찔렀어요 조회")
    void SUCCESS_getAllPokeMeHistory() {
        SimplePokeProfile simplePokeProfile = SimplePokeProfile.of(2L, 2L, "", "name2",
                "message", 34, "서버", 3, null, "name3 외 1명과 친구", false, false, false, "");

        PokeToMeHistoryList pokeToMeHistoryList = PokeToMeHistoryList.of(List.of(simplePokeProfile), 1, 1, 0);
        Page<PokeHistory> pokeHistoryPage = new PageImpl<>(List.of(pokeHistory2));
        Pageable pageable = Pageable.ofSize(1);

        when(pokeHistoryService.getPokeMeUserIds(1L)).thenReturn(List.of(2L));
        when(pokeHistoryService.getAllLatestPokeHistoryFromTo(2L, 1L)).thenReturn(List.of(pokeHistory2));
        when(pokeHistoryService.getAllLatestPokeHistoryIn(List.of(2L), pageable)).thenReturn(pokeHistoryPage);

        when(pokeService.getPokeDetail(pokeHistory2.getId())).thenReturn(pokeDetail2);
        when(userService.getUserProfile(2L)).thenReturn(userProfile2);
        when(playgroundAuthService.getPlaygroundMemberProfiles("token", List.of(2L))).thenReturn(
                playgroundProfileListWithoutImage);
        when(friendService.getMutualFriendIds(1L, 2L)).thenReturn(List.of(3L, 4L));
        when(userService.getUserProfile(3L)).thenReturn(userProfile3);
        when(userService.getUserProfile(4L)).thenReturn(userProfile4);
        when(friendService.getRelationInfo(1L, 2L)).thenReturn(relationship2);
        when(pokeHistoryService.getAllOfPokeBetween(2L, 1L)).thenReturn(
                List.of(pokeHistoryInfo2, pokeHistoryInfo2PokedIsReply));
        when(userService.getUserProfilesByPlaygroundIds(anyList())).thenReturn(userProfileList);

        PokeToMeHistoryList result = pokeFacade.getAllPokeMeHistory(user, pageable);
        assertEquals(pokeToMeHistoryList.getHistory().get(0).getUserId(), result.getHistory().get(0).getUserId());
        assertEquals(pokeToMeHistoryList.getTotalPageSize(), result.getTotalPageSize());
        assertEquals(pokeToMeHistoryList.getPageSize(), result.getPageSize());
        assertEquals(pokeToMeHistoryList.getPageNum(), result.getPageNum());
    }

    @Test
    @DisplayName("SUCCESS_친구 찌르기, 친구일 때")
    void SUCCESS_pokeFriendFriendEachOther() {
        when(pokeService.poke(1L, 2L, "message", false)).thenReturn(pokeHistory2);
        when(friendService.isFriendEachOther(1L, 2L)).thenReturn(true);

        Long result = pokeFacade.pokeFriend(1L, 2L, "message", false);
        assertEquals(2L, result);
    }

    @Test
    @DisplayName("SUCCESS_친구 찌르기, 친구 아닐 때, 찌른 적 없을 때")
    void SUCCESS_pokeFriendNotPokeBefore() {
        when(pokeService.poke(1L, 2L, "message", false)).thenReturn(pokeHistory2);
        when(friendService.isFriendEachOther(1L, 2L)).thenReturn(false);
        when(pokeHistoryService.getAllOfPokeBetween(1L, 2L)).thenReturn(List.of());

        Long result = pokeFacade.pokeFriend(1L, 2L, "message", false);
        assertEquals(2L, result);
    }

    @Test
    @DisplayName("SUCCESS_친구 찌르기, 친구 아닐 때, 찌른 적 있을 때")
    void SUCCESS_pokeFriendPokeBefore() {
        when(pokeService.poke(1L, 2L, "message", false)).thenReturn(pokeHistory2);
        when(friendService.isFriendEachOther(1L, 2L)).thenReturn(false);
        when(pokeHistoryService.getAllOfPokeBetween(1L, 2L)).thenReturn(List.of(pokeHistoryInfo2));

        Long result = pokeFacade.pokeFriend(1L, 2L, "message", false);
        assertEquals(2L, result);
    }

    @Test
    @DisplayName("SUCCESS_친구 가져오기, 이미지 있을 때, 친구 없을 때")
    void SUCCESS_getFriendNoMutualFriend() {
        SimplePokeProfile simplePokeProfile = SimplePokeProfile.of(2L, 2L, "image", "name2",
                "", 34, "서버", 1, null, "새로운 친구", false, false, false, "");

        when(friendService.getPokeFriendIdRandomly(1L)).thenReturn(2L);
        when(userService.getUserProfile(2L)).thenReturn(userProfile2);
        when(playgroundAuthService.getPlaygroundMemberProfiles("token", List.of(2L))).thenReturn(playgroundProfileList);
        when(friendService.getRelationInfo(1L, 2L)).thenReturn(relationship1);
        when(friendService.getMutualFriendIds(1L, 2L)).thenReturn(List.of());
        when(pokeHistoryService.getAllOfPokeBetween(1L, 2L)).thenReturn(List.of(pokeHistoryInfo2));

        List<SimplePokeProfile> result = pokeFacade.getFriend(user);
        assertEquals(List.of(simplePokeProfile), result);
    }

    @Test
    @DisplayName("SUCCESS_친구 가져오기, 이미지 있을 때, 친구 1명일 때")
    void SUCCESS_getFriendWithImage() {
        SimplePokeProfile simplePokeProfile = SimplePokeProfile.of(2L, 2L, "image", "name2",
                "", 34, "서버", 1, null, "name3의 친구", false, false, false, "");

        when(friendService.getPokeFriendIdRandomly(1L)).thenReturn(2L);
        when(userService.getUserProfile(2L)).thenReturn(userProfile2);
        when(playgroundAuthService.getPlaygroundMemberProfiles("token", List.of(2L))).thenReturn(playgroundProfileList);
        when(friendService.getRelationInfo(1L, 2L)).thenReturn(relationship1);
        when(friendService.getMutualFriendIds(1L, 2L)).thenReturn(List.of(3L));
        when(userService.getUserProfile(3L)).thenReturn(userProfile3);
        when(pokeHistoryService.getAllOfPokeBetween(1L, 2L)).thenReturn(List.of(pokeHistoryInfo2));

        List<SimplePokeProfile> result = pokeFacade.getFriend(user);
        assertEquals(List.of(simplePokeProfile), result);
    }

    @Test
    @DisplayName("SUCCESS_친구 가져오기, 이미지 없을 때, 친구 1명일 때")
    void SUCCESS_getFriendWithoutImage() {
        SimplePokeProfile simplePokeProfile = SimplePokeProfile.of(2L, 2L, "", "name2",
                "", 34, "서버", 1, null, "name3의 친구", false, false, false, "");

        when(friendService.getPokeFriendIdRandomly(1L)).thenReturn(2L);
        when(userService.getUserProfile(2L)).thenReturn(userProfile2);
        when(playgroundAuthService.getPlaygroundMemberProfiles("token", List.of(2L))).thenReturn(
                playgroundProfileListWithoutImage);
        when(friendService.getRelationInfo(1L, 2L)).thenReturn(relationship1);
        when(friendService.getMutualFriendIds(1L, 2L)).thenReturn(List.of(3L));
        when(userService.getUserProfile(3L)).thenReturn(userProfile3);
        when(pokeHistoryService.getAllOfPokeBetween(1L, 2L)).thenReturn(List.of(pokeHistoryInfo2));

        List<SimplePokeProfile> result = pokeFacade.getFriend(user);
        assertEquals(List.of(simplePokeProfile), result);
    }

    @Test
    @DisplayName("SUCCESS_친구 가져오기, 이미지 없을 때, 친구 2명 이상일 때")
    void SUCCESS_getFriendWithoutImageSomeFriends() {
        SimplePokeProfile simplePokeProfile = SimplePokeProfile.of(2L, 2L, "", "name2",
                "", 34, "서버", 1, null, "name3 외 1명과 친구", false, false, false, "");

        when(friendService.getPokeFriendIdRandomly(1L)).thenReturn(2L);
        when(userService.getUserProfile(2L)).thenReturn(userProfile2);
        when(playgroundAuthService.getPlaygroundMemberProfiles("token", List.of(2L))).thenReturn(
                playgroundProfileListWithoutImage);
        when(friendService.getRelationInfo(1L, 2L)).thenReturn(relationship1);
        when(friendService.getMutualFriendIds(1L, 2L)).thenReturn(List.of(3L, 4L));
        when(userService.getUserProfile(3L)).thenReturn(userProfile3);
        when(userService.getUserProfile(4L)).thenReturn(userProfile4);
        when(pokeHistoryService.getAllOfPokeBetween(1L, 2L)).thenReturn(List.of(pokeHistoryInfo2));

        List<SimplePokeProfile> result = pokeFacade.getFriend(user);
        assertEquals(List.of(simplePokeProfile), result);
    }

    @ParameterizedTest
    @EnumSource(value = Friendship.class)
    @DisplayName("SUCCESS_친구 관계로 두 친구 가져오기, 친구 아닐 때")
    void SUCCESS_getTwoFriendsByFriendshipNonFriend(Friendship friendship) {
        SimplePokeProfile simplePokeProfile = SimplePokeProfile.of(2L, 2L, "image", "name2",
                "message", 34, "서버", 3, null, "name3의 친구", false, true, false, "");

        when(friendService.findAllFriendsByFriendship(any(), any(), any())).thenReturn(List.of(friend2));
        when(pokeHistoryService.getAllOfPokeBetween(1L, 2L)).thenReturn(List.of(pokeHistoryInfo2));
        when(pokeService.getPokeDetail(pokeHistory2.getId())).thenReturn(pokeDetail2);
        when(userService.getUserProfile(2L)).thenReturn(userProfile2);
        when(playgroundAuthService.getPlaygroundMemberProfiles("token", List.of(2L))).thenReturn(playgroundProfileList);
        when(friendService.getMutualFriendIds(1L, 2L)).thenReturn(List.of(3L));
        when(userService.getUserProfile(3L)).thenReturn(userProfile3);
        when(friendService.getRelationInfo(1L, 2L)).thenReturn(relationship2);
        when(pokeHistoryService.getAllOfPokeBetween(2L, 1L)).thenReturn(
                List.of(pokeHistoryInfo2, pokeHistoryInfo2PokedIsNotReply));
        when(userService.getUserProfilesByPlaygroundIds(anyList())).thenReturn(userProfileList);

        List<SimplePokeProfile> result = pokeFacade.getTwoFriendByFriendship(user, friendship);
        assertEquals(List.of(simplePokeProfile), result);
    }

    @ParameterizedTest
    @EnumSource(value = Friendship.class)
    @DisplayName("SUCCESS_친구 관계로 친구 사이즈 가져오기")
    void SUCCESS_getFriendSizeByFriendship(Friendship friendship) {
        when(friendService.findAllFriendsByFriendship(1L, friendship.getLowerLimit(),
                friendship.getUpperLimit())).thenReturn(List.of(friend2));

        int result = pokeFacade.getFriendSizeByFriendship(user.getId(), friendship);
        assertEquals(1, result);
    }

    @ParameterizedTest
    @EnumSource(value = Friendship.class)
    @DisplayName("SUCCESS_친구 관계로 모든 친구 가져오기")
    void SUCCESS_getAllFriendByFriendship(Friendship friendship) {
        SimplePokeProfile simplePokeProfile = SimplePokeProfile.of(2L, 2L, "image", "name2",
                "message", 34, "서버", 3, null, "name3의 친구", false, false, false, "");
        Pageable pageable = Pageable.ofSize(1);
        Page<Friend> friendPage = new PageImpl<>(List.of(friend2));
        EachRelationFriendList eachRelationFriendList = EachRelationFriendList.of(List.of(simplePokeProfile),
                0, 0, 1, 0);

        when(friendService.findAllFriendsByFriendship(anyLong(), anyInt(), anyInt(), any())).thenReturn(friendPage);
        when(pokeService.getPokeDetail(pokeHistory2.getId())).thenReturn(pokeDetail2);
        when(userService.getUserProfile(2L)).thenReturn(userProfile2);
        when(playgroundAuthService.getPlaygroundMemberProfiles("token", List.of(2L))).thenReturn(playgroundProfileList);
        when(friendService.getMutualFriendIds(1L, 2L)).thenReturn(List.of(3L));
        when(userService.getUserProfile(3L)).thenReturn(userProfile3);
        when(friendService.getRelationInfo(1L, 2L)).thenReturn(relationship2);
        when(pokeHistoryService.getAllOfPokeBetween(any(), any())).thenReturn(
                List.of(pokeHistoryInfo2, pokeHistoryInfo2PokedIsNotReply));
        when(userService.getUserProfilesByPlaygroundIds(anyList())).thenReturn(userProfileList);

        EachRelationFriendList result = pokeFacade.getAllFriendByFriendship(user, friendship, pageable);
        assertEquals(eachRelationFriendList.getFriendList().get(0).getUserId(),
                result.getFriendList().get(0).getUserId());
        assertEquals(eachRelationFriendList.getPageSize(), result.getPageSize());
        assertEquals(eachRelationFriendList.getPageNum(), result.getPageNum());
    }

    @Test
    @DisplayName("SUCCESS_콕찌르기 히스토리 프로필 조회, 답장 없을 때, 친구 1명일 때")
    void SUCCESS_getPokeHistoryProfileReplyAMutualFriend() {
        SimplePokeProfile simplePokeProfile = SimplePokeProfile.of(2L, 2L, "image", "name2", "message",
                34, "서버", 3, null, "name3의 친구", false, true, false, "");

        when(pokeService.getPokeDetail(pokeHistory2.getId())).thenReturn(pokeDetail2);
        when(userService.getUserProfile(2L)).thenReturn(userProfile2);
        when(playgroundAuthService.getPlaygroundMemberProfiles("token", List.of(2L))).thenReturn(playgroundProfileList);
        when(friendService.getMutualFriendIds(1L, 2L)).thenReturn(List.of(3L));
        when(userService.getUserProfile(3L)).thenReturn(userProfile3);
        when(friendService.getRelationInfo(1L, 2L)).thenReturn(relationship2);
        when(pokeHistoryService.getAllOfPokeBetween(2L, 1L)).thenReturn(
                List.of(pokeHistoryInfo2, pokeHistoryInfo2PokedIsNotReply));
        when(userService.getUserProfilesByPlaygroundIds(anyList())).thenReturn(userProfileList);

        SimplePokeProfile result = pokeFacade.getPokeHistoryProfile(user, 2L, 2L);
        assertEquals(simplePokeProfile, result);
    }

    @Test
    @DisplayName("SUCCESS_콕찌르기 히스토리 프로필 조회, 답장 있을 때, 친구 2명 이상일 때")
    void SUCCESS_getPokeHistoryProfileNoReplySomeMutualFriends() {
        SimplePokeProfile simplePokeProfile = SimplePokeProfile.of(2L, 2L, "", "name2",
                "message", 34, "서버", 3, null, "name3 외 1명과 친구", false, false, false, "");

        when(pokeService.getPokeDetail(pokeHistory2.getId())).thenReturn(pokeDetail2);
        when(userService.getUserProfile(2L)).thenReturn(userProfile2);
        when(playgroundAuthService.getPlaygroundMemberProfiles("token", List.of(2L))).thenReturn(
                playgroundProfileListWithoutImage);
        when(friendService.getMutualFriendIds(1L, 2L)).thenReturn(List.of(3L, 4L));
        when(userService.getUserProfile(3L)).thenReturn(userProfile3);
        when(userService.getUserProfile(4L)).thenReturn(userProfile4);
        when(friendService.getRelationInfo(1L, 2L)).thenReturn(relationship2);
        when(pokeHistoryService.getAllOfPokeBetween(2L, 1L)).thenReturn(
                List.of(pokeHistoryInfo2, pokeHistoryInfo2PokedIsReply));
        when(userService.getUserProfilesByPlaygroundIds(anyList())).thenReturn(userProfileList);

        SimplePokeProfile result = pokeFacade.getPokeHistoryProfile(user, 2L, 2L);
        assertEquals(simplePokeProfile, result);
    }

    @Test
    @DisplayName("SUCCESS_콕찌르기 히스토리 프로필 조회, 답장 없을 때, 친구 없을 때")
    void SUCCESS_getPokeHistoryProfileNoReplyNoMutualFriend() {
        SimplePokeProfile simplePokeProfile = SimplePokeProfile.of(2L, 2L, "", "name2", "message",
                34, "서버", 3, null, "새로운 친구", false, true, false, "");

        when(pokeService.getPokeDetail(pokeHistory2.getId())).thenReturn(pokeDetail2);
        when(userService.getUserProfile(2L)).thenReturn(userProfile2);
        when(playgroundAuthService.getPlaygroundMemberProfiles("token", List.of(2L))).thenReturn(
                playgroundProfileListWithoutImage);
        when(friendService.getMutualFriendIds(1L, 2L)).thenReturn(List.of());
        when(friendService.getRelationInfo(1L, 2L)).thenReturn(relationship2);
        when(pokeHistoryService.getAllOfPokeBetween(2L, 1L)).thenReturn(
                List.of(pokeHistoryInfo2, pokeHistoryInfo2PokedIsNotReply));
        when(userService.getUserProfilesByPlaygroundIds(anyList())).thenReturn(userProfileList);

        SimplePokeProfile result = pokeFacade.getPokeHistoryProfile(user, 2L, 2L);
        assertEquals(simplePokeProfile, result);
    }

    @Test
    @DisplayName("SUCCESS_신규 유저인지 조회")
    void SUCCESS_getIsNewUser() {
        when(friendService.getIsNewUser(1L)).thenReturn(true);

        boolean result = pokeFacade.getIsNewUser(user.getId());
        assertTrue(result);
    }

    @Test
    @DisplayName("SUCCESS_모든 유형의 추천 친구 조회")
    void SUCCESS_getRecommendedFriendsByAllType() {
        // given
        final List<FriendRecommendType> friendRecommendTypes = List.of(FriendRecommendType.ALL);
        ActivityCardinalInfo cardinalInfo = ActivityCardinalInfo.builder().cardinalInfo("33,서버").build();
        String mbti = "ENFP";
        String university = "테스트대학교";
        Integer generation = 33;
        OwnPlaygroundProfile ownPlaygroundProfile = new OwnPlaygroundProfile(mbti, university, List.of(cardinalInfo));
        given(playgroundAuthService.getOwnPlaygroundProfile(anyString())).willReturn(ownPlaygroundProfile);

        PlaygroundActivity playgroundActivity = new PlaygroundActivity("아요", generation);

        PlaygroundProfileOfRecommendedFriend recommendedFriendByGeneration1 = PlaygroundProfileOfRecommendedFriend.builder()
                .playgroundId(1L).activities(List.of(playgroundActivity)).build();
        PlaygroundProfileOfRecommendedFriend recommendedFriendByGeneration2 = PlaygroundProfileOfRecommendedFriend.builder()
                .playgroundId(2L).activities(List.of(playgroundActivity)).build();
        PlaygroundProfileOfRecommendedFriend recommendedFriendByGeneration3 = PlaygroundProfileOfRecommendedFriend.builder()
                .playgroundId(3L).activities(List.of(playgroundActivity)).build();

        PlaygroundProfileOfRecommendedFriend recommendedFriendByMbti4 = PlaygroundProfileOfRecommendedFriend.builder()
                .mbti(mbti).playgroundId(4L).activities(List.of(playgroundActivity)).build();
        PlaygroundProfileOfRecommendedFriend recommendedFriendByMbti5 = PlaygroundProfileOfRecommendedFriend.builder()
                .mbti(mbti).playgroundId(5L).activities(List.of(playgroundActivity)).build();
        PlaygroundProfileOfRecommendedFriend recommendedFriendByMbti6 = PlaygroundProfileOfRecommendedFriend.builder()
                .mbti(mbti).playgroundId(6L).activities(List.of(playgroundActivity)).build();

        PlaygroundProfileOfRecommendedFriend recommendedFriendByUniversity7 = PlaygroundProfileOfRecommendedFriend.builder()
                .university(university).playgroundId(7L).activities(List.of(playgroundActivity)).build();
        PlaygroundProfileOfRecommendedFriend recommendedFriendByUniversity8 = PlaygroundProfileOfRecommendedFriend.builder()
                .university(university).playgroundId(8L).activities(List.of(playgroundActivity)).build();
        PlaygroundProfileOfRecommendedFriend recommendedFriendByUniversity9 = PlaygroundProfileOfRecommendedFriend.builder()
                .university(university).playgroundId(9L).activities(List.of(playgroundActivity)).build();

        given(playgroundAuthService.getPlaygroundProfilesForSameGeneration(generation)).willReturn(
                List.of(recommendedFriendByGeneration1, recommendedFriendByGeneration2,
                        recommendedFriendByGeneration3));
        given(playgroundAuthService.getPlaygroundProfilesForSameMbtiAndGeneration(generation, mbti)).willReturn(
                List.of(recommendedFriendByMbti4, recommendedFriendByMbti5, recommendedFriendByMbti6));
        given(playgroundAuthService.getPlaygroundProfilesForSameUniversityAndGeneration(generation,
                university)).willReturn(List.of(recommendedFriendByUniversity7, recommendedFriendByUniversity8,
                recommendedFriendByUniversity9));
        given(friendService.findUserIdsLinkedFriends(anyLong())).willReturn(new ArrayList<>());
        List<UserProfile> userProfiles = List.of(
                UserProfile.builder().userId(11L).playgroundId(1L).build(),
                UserProfile.builder().userId(22L).playgroundId(2L).build(),
                UserProfile.builder().userId(33L).playgroundId(3L).build(),
                UserProfile.builder().userId(44L).playgroundId(4L).build(),
                UserProfile.builder().userId(55L).playgroundId(5L).build(),
                UserProfile.builder().userId(66L).playgroundId(6L).build(),
                UserProfile.builder().userId(77L).playgroundId(7L).build(),
                UserProfile.builder().userId(88L).playgroundId(8L).build(),
                UserProfile.builder().userId(99L).playgroundId(9L).build()
        );
        given(userService.getUserProfilesByPlaygroundIds(anyList())).willReturn(userProfiles);
        User user1 = User.builder().playgroundId(100L).id(1000L).playgroundToken("token").build();

        // when
        RecommendedFriendsByAllType result = pokeFacade.getRecommendedFriendsByAllType(friendRecommendTypes, 6, user1);
        List<Long> playgroundIdByRecommendedFriendByGeneration = findPlaygroundIdsInRecommendedFriendsByAllTypeByType(
                result, FriendRecommendType.GENERATION);
        List<Long> playgroundIdByRecommendedFriendByMbti = findPlaygroundIdsInRecommendedFriendsByAllTypeByType(result,
                FriendRecommendType.MBTI);
        List<Long> playgroundIdByRecommendedFriendByUniversity = findPlaygroundIdsInRecommendedFriendsByAllTypeByType(
                result, FriendRecommendType.UNIVERSITY);

        // then
        assertEquals(3, result.getRandomInfoList().size());
        assertEquals(3, playgroundIdByRecommendedFriendByGeneration.size());
        assertTrue(playgroundIdByRecommendedFriendByGeneration.containsAll(List.of(1L, 2L, 3L)));
        assertEquals(3, playgroundIdByRecommendedFriendByMbti.size());
        assertTrue(playgroundIdByRecommendedFriendByMbti.containsAll(List.of(4L, 5L, 6L)));
        assertEquals(3, playgroundIdByRecommendedFriendByUniversity.size());
        assertTrue(playgroundIdByRecommendedFriendByUniversity.containsAll(List.of(7L, 8L, 9L)));
    }

    private List<Long> findPlaygroundIdsInRecommendedFriendsByAllTypeByType(
            RecommendedFriendsByAllType recommendedFriendsByAllType, FriendRecommendType type) {
        return recommendedFriendsByAllType.getRandomInfoList().stream()
                .filter(randomInfo -> randomInfo.getRandomType() == type)
                .flatMap(randomInfo -> randomInfo.getUserInfoList().stream())
                .map(SimplePokeProfile::getPlaygroundId)
                .toList();
    }
}
