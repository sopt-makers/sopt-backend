package org.sopt.app.application;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.sopt.app.application.friend.FriendService;
import org.sopt.app.common.utils.AnonymousNameGenerator;
import org.sopt.app.interfaces.postgres.FriendRepository;

@ExtendWith(MockitoExtension.class)
class FriendServiceTest {

    @Mock
    private FriendRepository friendRepository;

    @Mock
    private AnonymousNameGenerator anonymousNameGenerator;

    @InjectMocks
    private FriendService friendService;

    @Test
    @DisplayName("SUCCESS_친구가 없다면 새로운 유저로 판단하여 true 반환")
    void getIsNewUserSuccess() {
        //given
        final Long anyUserId = 1L;
        //when
        when(friendRepository.findAllByFriendUserId(anyUserId)).thenReturn(List.of());
        when(friendRepository.findAllByUserIdAndFriendUserIdIn(anyUserId, List.of())).thenReturn(List.of());
        //then
        Assertions.assertTrue(friendService.getIsNewUser(anyUserId));
    }

    @Test
    @DisplayName("SUCCESS_이미 존재하는 친구 관계라면 저장하지 않음")
    void registerFriendshipOf_이미_존재하는_경우_저장하지_않음() {
        //given
        Long userId = 1L;
        Long friendId = 2L;
        when(friendRepository.existsByUserIdAndFriendUserId(userId, friendId)).thenReturn(true);

        //when
        friendService.registerFriendshipOf(userId, friendId);

        //then
        verify(friendRepository, times(0)).save(any());
    }
}
