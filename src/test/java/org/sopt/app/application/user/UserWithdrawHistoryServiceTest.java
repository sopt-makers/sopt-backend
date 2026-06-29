package org.sopt.app.application.user;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.sopt.app.domain.entity.UserWithdrawHistory;
import org.sopt.app.interfaces.postgres.UserWithdrawHistoryRepository;

@ExtendWith(MockitoExtension.class)
@DisplayName("UserWithdrawHistoryService 단위 테스트")
class UserWithdrawHistoryServiceTest {

    @Mock
    private UserWithdrawHistoryRepository userWithdrawHistoryRepository;

    @InjectMocks
    private UserWithdrawHistoryService userWithdrawHistoryService;

    @Test
    @DisplayName("SUCCESS_탈퇴 요청 시 인증된 요청자의 ID로 탈퇴 이력이 저장된다")
    void SUCCESS_recordWithdrawRequest_savesRequesterId() {
        // given
        final Long userId = 1L;
        // when
        userWithdrawHistoryService.recordWithdrawRequest(userId);

        // then
        ArgumentCaptor<UserWithdrawHistory> captor = ArgumentCaptor.forClass(UserWithdrawHistory.class);
        verify(userWithdrawHistoryRepository, times(1)).save(captor.capture());
        assertThat(captor.getValue().getUserId()).isEqualTo(userId);
    }
}
