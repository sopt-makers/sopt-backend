package org.sopt.app.application;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.sopt.app.application.user.UserWithdrawService;
import org.sopt.app.common.event.EventPublisher;
import org.sopt.app.interfaces.postgres.UserRepository;

@ExtendWith(MockitoExtension.class)
class UserWithdrawTest {

    @Mock
    private EventPublisher eventPublisher;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserWithdrawService userWithdrawService;

    @Test
    void SUCCESS_유저_탈퇴시_유저_삭제() {
        //given
        final Long userId = 1L;

        // when
        userWithdrawService.withdrawUser(userId);

        //then
        verify(userRepository).deleteById(userId);
    }

    @Test
    void SUCCESS_유저_탈퇴시_스탬프_삭제_이벤트_발생() {
        //given
        final Long userId = 1L;

        // when
        userWithdrawService.withdrawUser(userId);

        //then
        verify(eventPublisher).raise(any());
    }
}
