package org.sopt.app.facade;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;

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
import org.sopt.app.application.auth.PlaygroundAuthInfo.MemberProfile;
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
import org.sopt.app.domain.enums.Friendship;
import org.sopt.app.presentation.poke.PokeResponse;
import org.sopt.app.presentation.poke.PokeResponse.EachRelationFriendList;
import org.sopt.app.presentation.poke.PokeResponse.PokeToMeHistoryList;
import org.sopt.app.presentation.poke.PokeResponse.SimplePokeProfile;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

@ExtendWith(MockitoExtension.class)
public class PokeFacadeTest {

    private static final String MESSAGES_HEADER_FOR_POKE = "함께 보낼 메시지를 선택해주세요";
    private Relationship relationship1 = Relationship.builder().pokeNum(1).build();
    private Relationship relationship2 = Relationship.builder().pokeNum(3).build();
    private PlaygroundAuthInfo.ActiveUserIds activeUserIds = new ActiveUserIds();
    private User user = User.builder().id(1L).playgroundToken("token").build();
    private UserProfile userProfile1 = UserProfile.builder().userId(1L).name("name1").playgroundId(1L).build();
    private UserProfile userProfile2 = UserProfile.builder().userId(2L).name("name2").playgroundId(2L).build();
    private UserProfile userProfile3 = UserProfile.builder().userId(3L).name("name3").playgroundId(3L).build();
    private UserProfile userProfile4 = UserProfile.builder().userId(4L).name("name4").playgroundId(4L).build();
    private List<UserProfile> userProfileList = List.of(userProfile2, userProfile3);
    private List<UserProfile> userProfileListIncludingMe = List.of(userProfile1, userProfile2, userProfile3);
    private ActivityCardinalInfo activityCardinalInfo = ActivityCardinalInfo.builder().cardinalInfo("34,서버").build();
    private List<MemberProfile> memberProfileList = List.of(
            new MemberProfile(2L, "image", "name2", List.of(activityCardinalInfo)),
            new MemberProfile(3L, "image", "name3", List.of(activityCardinalInfo))
    );
    private List<MemberProfile> memberProfileListWithoutImage = List.of(
            new MemberProfile(2L, null, "name2", List.of(activityCardinalInfo)),
            new MemberProfile(3L, null, "name3", List.of(activityCardinalInfo))
    );
    private List<Long> userIdListExcludeMe = List.of(2L, 3L);
    private List<PokeResponse.Friend> friendList = List.of(
            PokeResponse.Friend.of(2L, 2L, "name2", "", List.of()),
            PokeResponse.Friend.of(3L, 3L, "name2", "", List.of())
    );
    private PokeHistory pokeHistory2 = PokeHistory.builder().id(2L).pokedId(1L).pokerId(2L).isReply(false)
            .isAnonymous(false).build();
    private PokeHistory pokeHistory2PokedIsNotReply = PokeHistory.builder().id(3L).pokedId(2L).pokerId(1L)
            .isReply(false).isAnonymous(false).build();
    private PokeHistory pokeHistory2PokedIsReply = PokeHistory.builder().id(3L).pokedId(2L).pokerId(1L).isReply(true)
            .isAnonymous(false).build();
    private PokeHistoryInfo pokeHistoryInfo2 = PokeHistoryInfo.builder().id(2L).pokedId(1L).pokerId(2L).isReply(false)
            .isAnonymous(false).build();
    private PokeHistoryInfo pokeHistoryInfo2PokedIsNotReply = PokeHistoryInfo.builder().id(3L).pokedId(2L).pokerId(1L)
            .isReply(false).isAnonymous(false).build();
    private PokeHistoryInfo pokeHistoryInfo2PokedIsReply = PokeHistoryInfo.builder().id(3L).pokedId(2L).pokerId(1L)
            .isReply(true)
            .isAnonymous(false).build();
    private PokeHistory pokeHistory3 = PokeHistory.builder().id(3L).pokedId(1L).pokerId(3L).isReply(true)
            .isAnonymous(false).build();
    private PokeDetail pokeDetail2 = PokeDetail.builder().id(2L).pokedId(1L).pokerId(2L).message("message").build();
    private Friend friend2 = Friend.builder().id(2L).userId(1L).friendUserId(2L).pokeCount(1).anonymousName("").build();
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
        List<PokeMessage> pokeMessageList = List.of(pokeMessage);
        PokeResponse.PokeMessage pokeMessageResponse = PokeResponse.PokeMessage.of(1L, "content");
        List<PokeResponse.PokeMessage> pokeMessageListResponse = List.of(pokeMessageResponse);

        when(pokeMessageService.pickRandomMessageByTypeOf(any())).thenReturn(pokeMessageList);

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
        assertEquals(pokingMessageHeader, MESSAGES_HEADER_FOR_POKE);
    }

    @Test
    @DisplayName("SUCCESS_신규 유저 추천 조회")
    void SUCCESS_getRecommendUserForNew() {
        MemberProfile memberProfile = new MemberProfile(2L, "image", "name2", List.of(activityCardinalInfo));
        List<MemberProfile> memberProfileListForNew = List.of(memberProfile);
        List<SimplePokeProfile> simplePokeProfileListForNew = List.of(
                SimplePokeProfile.of(2L, 2L, "image", "name2", "", 34, "서버", 1,
                        "새로운 친구", "새로운 친구", true, false, false, ""));

        when(playgroundAuthService.getPlayGroundUserIds("token")).thenReturn(activeUserIds);
        when(userService.getUserProfilesByPlaygroundIds(activeUserIds.getUserIds())).thenReturn(
                userProfileListIncludingMe);
        when(friendService.isFriendEachOther(1L, 2L)).thenReturn(false);
        when(friendService.isFriendEachOther(1L, 3L)).thenReturn(true);
        when(playgroundAuthService.getPlaygroundMemberProfiles(any(), any())).thenReturn(memberProfileListForNew);
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
        when(playgroundAuthService.getPlaygroundMemberProfiles("token", List.of(2L))).thenReturn(memberProfileList);
        when(playgroundAuthService.getPlaygroundMemberProfiles("token", List.of(3L))).thenReturn(
                memberProfileListWithoutImage);
        when(playgroundAuthService.getPlaygroundMemberProfiles("token", List.of(2L, 3L))).thenReturn(
                memberProfileListWithoutImage);
        when(friendService.findAllFriendIdsByUserIdRandomlyExcludeUserId(any(), any(), anyInt())).thenReturn(
                userIdListExcludeMe);
        when(userService.getUserProfileByUserId(userIdListExcludeMe)).thenReturn(userProfileList);
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
        when(playgroundAuthService.getPlaygroundMemberProfiles("token", List.of(2L))).thenReturn(memberProfileList);
        when(playgroundAuthService.getPlaygroundMemberProfiles("token", List.of(3L))).thenReturn(
                memberProfileListWithoutImage);
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
        when(userService.getUserProfilesByPlaygroundIds(List.of(2L))).thenReturn(List.of(userProfile2));
        when(playgroundAuthService.getPlaygroundMemberProfiles("token", List.of(2L))).thenReturn(memberProfileList);
        when(friendService.getMutualFriendIds(1L, 2L)).thenReturn(List.of(3L));
        when(userService.getUserProfile(3L)).thenReturn(userProfile3);
        when(friendService.getRelationInfo(1L, 2L)).thenReturn(relationship1);

        SimplePokeProfile result = pokeFacade.getMostRecentPokeMeHistory(user);
        assertEquals(simplePokeProfile, result);
    }

    @Test
    @DisplayName("SUCCESS_단일 누가 나를 찔렀어요 답장 O 조회")
    void SUCCESS_getMostRecentPokeMeHistoryIsReply() {
        when(pokeHistoryService.getPokeMeUserIds(1L)).thenReturn(List.of(3L));
        when(pokeHistoryService.getAllLatestPokeHistoryFromTo(3L, 1L)).thenReturn(List.of(pokeHistory3));

        SimplePokeProfile result = pokeFacade.getMostRecentPokeMeHistory(user);
        assertEquals(null, result);
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
        when(userService.getUserProfilesByPlaygroundIds(List.of(2L))).thenReturn(List.of(userProfile2));
        when(playgroundAuthService.getPlaygroundMemberProfiles("token", List.of(2L))).thenReturn(
                memberProfileListWithoutImage);
        when(friendService.getMutualFriendIds(1L, 2L)).thenReturn(List.of(3L, 4L));
        when(userService.getUserProfile(3L)).thenReturn(userProfile3);
        when(userService.getUserProfile(4L)).thenReturn(userProfile4);
        when(friendService.getRelationInfo(1L, 2L)).thenReturn(relationship2);
        when(pokeHistoryService.getAllOfPokeBetween(2L, 1L)).thenReturn(
                List.of(pokeHistoryInfo2, pokeHistoryInfo2PokedIsReply));

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

        when(friendService.getPokeFriendIdRandomly(1L)).thenReturn(List.of(2L, 3L));
        when(userService.getUserProfileByUserId(List.of(2L, 3L))).thenReturn(List.of(userProfile2, userProfile3));
        when(playgroundAuthService.getPlaygroundMemberProfiles("token", List.of(2L, 3L))).thenReturn(memberProfileList);
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

        when(friendService.getPokeFriendIdRandomly(1L)).thenReturn(List.of(2L, 3L));
        when(userService.getUserProfileByUserId(List.of(2L, 3L))).thenReturn(List.of(userProfile2, userProfile3));
        when(playgroundAuthService.getPlaygroundMemberProfiles("token", List.of(2L, 3L))).thenReturn(memberProfileList);
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

        when(friendService.getPokeFriendIdRandomly(1L)).thenReturn(List.of(2L, 3L));
        when(userService.getUserProfileByUserId(List.of(2L, 3L))).thenReturn(List.of(userProfile2, userProfile3));
        when(playgroundAuthService.getPlaygroundMemberProfiles("token", List.of(2L, 3L))).thenReturn(
                memberProfileListWithoutImage);
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

        when(friendService.getPokeFriendIdRandomly(1L)).thenReturn(List.of(2L, 3L));
        when(userService.getUserProfileByUserId(List.of(2L, 3L))).thenReturn(List.of(userProfile2, userProfile3));
        when(playgroundAuthService.getPlaygroundMemberProfiles("token", List.of(2L, 3L))).thenReturn(
                memberProfileListWithoutImage);
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
        when(userService.getUserProfilesByPlaygroundIds(List.of(2L))).thenReturn(List.of(userProfile2));
        when(playgroundAuthService.getPlaygroundMemberProfiles("token", List.of(2L))).thenReturn(memberProfileList);
        when(friendService.getMutualFriendIds(1L, 2L)).thenReturn(List.of(3L));
        when(userService.getUserProfile(3L)).thenReturn(userProfile3);
        when(friendService.getRelationInfo(1L, 2L)).thenReturn(relationship2);
        when(pokeHistoryService.getAllOfPokeBetween(2L, 1L)).thenReturn(
                List.of(pokeHistoryInfo2, pokeHistoryInfo2PokedIsNotReply));

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
        when(userService.getUserProfilesByPlaygroundIds(List.of(2L))).thenReturn(List.of(userProfile2));
        when(playgroundAuthService.getPlaygroundMemberProfiles("token", List.of(2L))).thenReturn(memberProfileList);
        when(friendService.getMutualFriendIds(1L, 2L)).thenReturn(List.of(3L));
        when(userService.getUserProfile(3L)).thenReturn(userProfile3);
        when(friendService.getRelationInfo(1L, 2L)).thenReturn(relationship2);
        when(pokeHistoryService.getAllOfPokeBetween(1L, 2L)).thenReturn(
                List.of(pokeHistoryInfo2, pokeHistoryInfo2PokedIsNotReply));

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
        when(userService.getUserProfilesByPlaygroundIds(List.of(2L))).thenReturn(List.of(userProfile2));
        when(playgroundAuthService.getPlaygroundMemberProfiles("token", List.of(2L))).thenReturn(memberProfileList);
        when(friendService.getMutualFriendIds(1L, 2L)).thenReturn(List.of(3L));
        when(userService.getUserProfile(3L)).thenReturn(userProfile3);
        when(friendService.getRelationInfo(1L, 2L)).thenReturn(relationship2);
        when(pokeHistoryService.getAllOfPokeBetween(2L, 1L)).thenReturn(
                List.of(pokeHistoryInfo2, pokeHistoryInfo2PokedIsNotReply));

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
        when(userService.getUserProfilesByPlaygroundIds(List.of(2L))).thenReturn(List.of(userProfile2));
        when(playgroundAuthService.getPlaygroundMemberProfiles("token", List.of(2L))).thenReturn(
                memberProfileListWithoutImage);
        when(friendService.getMutualFriendIds(1L, 2L)).thenReturn(List.of(3L, 4L));
        when(userService.getUserProfile(3L)).thenReturn(userProfile3);
        when(userService.getUserProfile(4L)).thenReturn(userProfile4);
        when(friendService.getRelationInfo(1L, 2L)).thenReturn(relationship2);
        when(pokeHistoryService.getAllOfPokeBetween(2L, 1L)).thenReturn(
                List.of(pokeHistoryInfo2, pokeHistoryInfo2PokedIsReply));

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
        when(userService.getUserProfilesByPlaygroundIds(List.of(2L))).thenReturn(List.of(userProfile2));
        when(playgroundAuthService.getPlaygroundMemberProfiles("token", List.of(2L))).thenReturn(
                memberProfileListWithoutImage);
        when(friendService.getMutualFriendIds(1L, 2L)).thenReturn(List.of());
        when(friendService.getRelationInfo(1L, 2L)).thenReturn(relationship2);
        when(pokeHistoryService.getAllOfPokeBetween(2L, 1L)).thenReturn(
                List.of(pokeHistoryInfo2, pokeHistoryInfo2PokedIsNotReply));

        SimplePokeProfile result = pokeFacade.getPokeHistoryProfile(user, 2L, 2L);
        assertEquals(simplePokeProfile, result);
    }

    @Test
    @DisplayName("SUCCESS_신규 유저인지 조회")
    void SUCCESS_getIsNewUser() {
        when(friendService.getIsNewUser(1L)).thenReturn(true);

        boolean result = pokeFacade.getIsNewUser(user.getId());
        assertEquals(true, result);
    }
}
