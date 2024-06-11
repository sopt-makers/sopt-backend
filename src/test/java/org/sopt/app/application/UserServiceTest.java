package org.sopt.app.application;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.sopt.app.application.auth.PlaygroundAuthInfo.PlaygroundMain;
import org.sopt.app.application.user.UserInfo;
import org.sopt.app.application.user.UserService;
import org.sopt.app.common.exception.UnauthorizedException;
import org.sopt.app.domain.entity.User;
import org.sopt.app.interfaces.postgres.UserRepository;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserService userService;

    @Test
    @DisplayName("SUCCESS_등록된 유저가 없으면 플레이그라운드 아이디로 회원가입")
    void SUCCESS_loginWithUserPlaygroundIdWithNoRegisteredUser() {
        //given
        final Long anyUserId = anyLong();
        PlaygroundMain playgroundMemberResponse = new PlaygroundMain();
        playgroundMemberResponse.setId(anyUserId);

        //when
        when(userRepository.findUserByPlaygroundId(anyUserId)).thenReturn(Optional.empty());
        when(userRepository.save(any(User.class))).thenReturn(User.builder().id(playgroundMemberResponse.getId()).build());

        UserInfo.Id expected = UserInfo.Id.builder().id(playgroundMemberResponse.getId()).build();
        UserInfo.Id result = userService.loginWithUserPlaygroundId(playgroundMemberResponse);

        //then
        Assertions.assertEquals(expected.getId(), result.getId());
    }

    @Test
    @DisplayName("SUCCESS_등록된 유저가 있으면 플레이그라운드 아이디로 로그인")
    void SUCCESS_loginWithUserPlaygroundIdWithRegisteredUser() {
        //given
        final Long anyUserId = anyLong();
        PlaygroundMain playgroundMemberResponse = new PlaygroundMain();
        playgroundMemberResponse.setId(anyUserId);

        User registeredUser = User.builder().id(anyUserId).build();

        //when
        when(userRepository.findUserByPlaygroundId(anyUserId)).thenReturn(Optional.of(registeredUser));
        when(userRepository.save(any(User.class))).thenReturn(registeredUser);

        UserInfo.Id expected = UserInfo.Id.builder().id(anyUserId).build();
        UserInfo.Id result = userService.loginWithUserPlaygroundId(playgroundMemberResponse);

        //then
        Assertions.assertEquals(expected.getId(), result.getId());
    }

    @Test
    @DisplayName("SUCCESS_유저 삭제")
    void SUCCESS_deleteUser() {
        //given
        User user = new User();

        //then
        Assertions.assertDoesNotThrow(() -> userService.deleteUser(user));
    }

    @Test
    @DisplayName("SUCCESS_플레이그라운드 토큰 조회")
    void SUCCESS_getPlaygroundToken() {
        //given
        final Long anyUserId = anyLong();
        final String playgroundToken = "token";
        final UserInfo.Id userId = UserInfo.Id.builder().id(anyUserId).build();
        User user = User.builder().id(anyUserId).playgroundToken(playgroundToken).build();

        //when
        when(userRepository.findUserById(anyUserId)).thenReturn(Optional.of(user));

        //then
        Assertions.assertEquals(playgroundToken, userService.getPlaygroundToken(userId).getAccessToken());
    }

    @Test
    @DisplayName("FAIL_플레이그라운드 토큰 조회시 유저를 찾지 못하면 UnauthorizedException 발생")
    void FAIL_getPlaygroundToken() {
        //given
        final Long anyUserId = anyLong();
        final UserInfo.Id userId = UserInfo.Id.builder().id(anyUserId).build();

        //when
        when(userRepository.findUserById(anyUserId)).thenReturn(Optional.empty());

        //then
        Assertions.assertThrows(UnauthorizedException.class, () -> userService.getPlaygroundToken(userId));
    }


    @Test
    @DisplayName("SUCCESS_플레이그라운드 토큰 업데이트")
    void SUCCESS_updatePlaygroundToken() {
        //given
        final Long anyUserId = anyLong();
        final UserInfo.Id userId = UserInfo.Id.builder().id(anyUserId).build();
        final String playgroundToken = "newToken";

        //when
        when(userRepository.findUserById(anyUserId)).thenReturn(Optional.of(User.builder().id(anyUserId).build()));
        when(userRepository.save(any(User.class))).thenReturn(User.builder().id(anyUserId).playgroundToken(playgroundToken).build());

        //then
        Assertions.assertDoesNotThrow(() -> userService.updatePlaygroundToken(userId, playgroundToken));
    }

    @Test
    @DisplayName("FAIL_플레이그라운드 토큰 업데이트시 유저를 찾지 못하면 UnauthorizedException 발생")
    void FAIL_updatePlaygroundToken() {
        //given
        final Long anyUserId = anyLong();
        final UserInfo.Id userId = UserInfo.Id.builder().id(anyUserId).build();
        final String playgroundToken = "newToken";

        //when
        when(userRepository.findUserById(anyUserId)).thenReturn(Optional.empty());

        //then
        Assertions.assertThrows(UnauthorizedException.class, () -> userService.updatePlaygroundToken(userId, playgroundToken));
    }

    @Test
    @DisplayName("SUCCESS_유저 프로필 조회")
    void SUCCESS_getUserProfileOrElseThrow() {
        //given
        final Long anyUserId = anyLong();
        final String username = "username";
        final String playgroundToken = "playgroundToken";
        final Long playgroundId = 1L;
        final User user = User.builder().id(anyUserId).username(username).playgroundId(playgroundId)
                .playgroundToken(playgroundToken).build();

        //when
        when(userRepository.findUserById(anyUserId)).thenReturn(Optional.of(user));

        UserInfo.UserProfile result = userService.getUserProfileOrElseThrow(anyUserId);
        UserInfo.UserProfile expected = UserInfo.UserProfile.builder().userId(anyUserId).name(username)
                .playgroundId(playgroundId).build();

        //then
        assertThat(result).usingRecursiveComparison().isEqualTo(expected);
    }

    @Test
    @DisplayName("FAIL_유저 프로필 조회시 유저를 찾지 못하면 UnauthorizedException 발생")
    void FAIL_getUserProfileOrElseThrow() {
        //given
        final Long anyUserId = anyLong();

        //when
        when(userRepository.findUserById(anyUserId)).thenReturn(Optional.empty());

        //then
        Assertions.assertThrows(UnauthorizedException.class, () -> userService.getUserProfileOrElseThrow(anyUserId));
    }

    @Test
    @DisplayName("SUCCESS_플레이그라운드 아이디 리스트로 유저 리스트 조회")
    void SUCCESS_getUserProfilesByPlaygroundIdsOrElseThrow() {
        //given
        final List<Long> playgroundIds = List.of(1L, 2L);
        final User user1 = User.builder().id(1L).username("user1").playgroundId(1L).build();
        final User user2 = User.builder().id(2L).username("user2").playgroundId(2L).build();
        final List<User> users = List.of(user1, user2);

        //when
        when(userRepository.findAllByPlaygroundIdIn(playgroundIds)).thenReturn(users);

        List<UserInfo.UserProfile> result = userService.getUserProfilesByPlaygroundIds(playgroundIds);
        List<UserInfo.UserProfile> expected = List.of(
                UserInfo.UserProfile.builder().userId(user1.getId()).name(user1.getUsername()).playgroundId(
                        user1.getPlaygroundId()).build(),
                UserInfo.UserProfile.builder().userId(user2.getId()).name(user2.getUsername()).playgroundId(
                        user2.getPlaygroundId()).build()
        );

        //then
        assertThat(result).usingRecursiveComparison().isEqualTo(expected);
    }

    @Test
    @DisplayName("SUCCESS_유저 아이디로 유저 프로필 조회")
    void SUCCESS_getUserProfilesByUserIdsOrElseThrow() {
        //given
        final List<Long> userIds = List.of(1L, 2L);
        final User user1 = User.builder().id(1L).username("user1").playgroundId(1L).build();
        final User user2 = User.builder().id(2L).username("user2").playgroundId(2L).build();
        final List<User> users = List.of(user1, user2);

        //when
        when(userRepository.findAllByIdIn(userIds)).thenReturn(users);

        List<UserInfo.UserProfile> result = userService.getUserProfilesByUserIds(userIds);
        List<UserInfo.UserProfile> expected = List.of(
                UserInfo.UserProfile.builder().userId(user1.getId()).name(user1.getUsername()).playgroundId(
                        user1.getPlaygroundId()).build(),
                UserInfo.UserProfile.builder().userId(user2.getId()).name(user2.getUsername()).playgroundId(
                        user2.getPlaygroundId()).build()
        );

        //then
        assertThat(result).usingRecursiveComparison().isEqualTo(expected);

    }
}