package org.sopt.app.application;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;

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
    void getPlaygroundToken() {
    }

    /* TODO: Implement following test cases
    @Test
    void updatePlaygroundToken() {
    }

    @Test
    void getUserProfile() {
    }

    @Test
    void getUserProfilesByPlaygroundIds() {
    }

    @Test
    void getUserProfileByUserId() {
    }

     */
}