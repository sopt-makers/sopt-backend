package org.sopt.app.application;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.sopt.app.application.playground.dto.PlaygroundProfileInfo.PlaygroundMain;
import org.sopt.app.application.user.*;
import org.sopt.app.common.exception.*;
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
    void SUCCESS_upsertNoRegisteredUser() {
        //given
        final Long anyUserId = anyLong();
        PlaygroundMain playgroundMemberResponse = PlaygroundMain.builder().id(anyUserId).build();

        //when
        when(userRepository.findUserByPlaygroundId(anyUserId)).thenReturn(Optional.empty());
        when(userRepository.save(any(User.class))).thenReturn(User.builder().id(playgroundMemberResponse.getId()).build());

        Long expected = playgroundMemberResponse.getId();
        Long result = userService.upsertUser(playgroundMemberResponse);

        //then
        Assertions.assertEquals(expected, result);
    }

    @Test
    @DisplayName("SUCCESS_등록된 유저가 있으면 플레이그라운드 아이디로 로그인")
    void SUCCESS_upsertRegisteredUser() {
        //given
        final Long anyUserId = anyLong();
        PlaygroundMain playgroundMemberResponse = PlaygroundMain.builder().id(anyUserId).build();

        User registeredUser = User.builder().id(anyUserId).build();

        //when
        when(userRepository.findUserByPlaygroundId(anyUserId)).thenReturn(Optional.of(registeredUser));
        when(userRepository.save(any(User.class))).thenReturn(registeredUser);

        Long result = userService.upsertUser(playgroundMemberResponse);

        //then
        Assertions.assertEquals(anyUserId, result);
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
        User user = User.builder().id(anyUserId).playgroundToken(playgroundToken).build();

        //when
        when(userRepository.findUserById(anyUserId)).thenReturn(Optional.of(user));

        //then
        Assertions.assertEquals(playgroundToken, userService.getPlaygroundToken(anyUserId).getAccessToken());
    }

    @Test
    @DisplayName("FAIL_플레이그라운드 토큰 조회시 유저를 찾지 못하면 UnauthorizedException 발생")
    void FAIL_getPlaygroundToken() {
        //given
        final Long anyUserId = anyLong();

        //when
        when(userRepository.findUserById(anyUserId)).thenReturn(Optional.empty());

        //then
        Assertions.assertThrows(UnauthorizedException.class, () -> userService.getPlaygroundToken(anyUserId));
    }


    @Test
    @DisplayName("SUCCESS_플레이그라운드 토큰 업데이트")
    void SUCCESS_updatePlaygroundToken() {
        //given
        final Long anyUserId = anyLong();
        final String playgroundToken = "newToken";

        //when
        when(userRepository.findUserById(anyUserId)).thenReturn(Optional.of(User.builder().id(anyUserId).build()));
        when(userRepository.save(any(User.class))).thenReturn(User.builder().id(anyUserId).playgroundToken(playgroundToken).build());

        //then
        Assertions.assertDoesNotThrow(() -> userService.updatePlaygroundToken(anyUserId, playgroundToken));
    }

    @Test
    @DisplayName("FAIL_플레이그라운드 토큰 업데이트시 유저를 찾지 못하면 UnauthorizedException 발생")
    void FAIL_updatePlaygroundToken() {
        //given
        final Long anyUserId = anyLong();

        final String playgroundToken = "newToken";

        //when
        when(userRepository.findUserById(anyUserId)).thenReturn(Optional.empty());

        //then
        Assertions.assertThrows(UnauthorizedException.class, () -> userService.updatePlaygroundToken(anyUserId, playgroundToken));
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

        UserProfile result = userService.getUserProfileOrElseThrow(anyUserId);
        UserProfile expected = UserProfile.builder().userId(anyUserId).name(username)
                .playgroundId(playgroundId).build();

        //then
        assertThat(result).usingRecursiveComparison().isEqualTo(expected);
    }

    @Test
    @DisplayName("FAIL_유저 프로필 조회시 유저를 찾지 못하면 NotFoundException 발생")
    void FAIL_getUserProfileOrElseThrow() {
        //given
        final Long anyUserId = anyLong();

        //when
        when(userRepository.findUserById(anyUserId)).thenReturn(Optional.empty());

        //then
        Assertions.assertThrows(NotFoundException.class, () -> userService.getUserProfileOrElseThrow(anyUserId));
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

        List<UserProfile> result = userService.getUserProfilesByPlaygroundIds(playgroundIds);
        List<UserProfile> expected = List.of(
                UserProfile.builder().userId(user1.getId()).name(user1.getUsername()).playgroundId(
                        user1.getPlaygroundId()).build(),
                UserProfile.builder().userId(user2.getId()).name(user2.getUsername()).playgroundId(
                        user2.getPlaygroundId()).build()
        );

        //then
        assertThat(result).usingRecursiveComparison().isEqualTo(expected);
    }
}