package org.sopt.app.application;

import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;

import java.util.List;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.sopt.app.application.poke.FriendService;
import org.sopt.app.interfaces.postgres.friend.FriendRepository;

@ExtendWith(MockitoExtension.class)
class FriendServiceTest {

    @Mock
    private FriendRepository friendRepository;

    @InjectMocks
    private FriendService friendService;

    @Test
    @DisplayName("SUCCESS_친구가 없다면 새로운 유저로 판단하여 true 반환")
    void getIsNewUserSuccess() {
        //given
        final Long anyUserId = anyLong();
        //when
        when(friendRepository.findAllByFriendUserId(anyUserId)).thenReturn(List.of());
        when(friendRepository.findAllByUserIdAndFriendUserIdIn(anyUserId, List.of())).thenReturn(List.of());
        //then
        Assertions.assertTrue(friendService.getIsNewUser(anyUserId));
    }
}