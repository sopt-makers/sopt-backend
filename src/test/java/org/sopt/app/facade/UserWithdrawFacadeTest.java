package org.sopt.app.facade;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.sopt.app.application.appservice.OperationConfigService;
import org.sopt.app.application.user.UserWithdrawInfo;
import org.sopt.app.application.user.UserWithdrawHistoryService;
import org.sopt.app.common.config.OperationConfigCategory;

@ExtendWith(MockitoExtension.class)
@DisplayName("UserWithdrawFacade 단위 테스트")
class UserWithdrawFacadeTest {

    @Mock
    private OperationConfigService operationConfigService;

    @Mock
    private UserWithdrawHistoryService userWithdrawHistoryService;

    @InjectMocks
    private UserWithdrawFacade userWithdrawFacade;

    @Test
    @DisplayName("SUCCESS_탈퇴 요청 시 요청을 기록하고 폼 URL을 반환한다")
    void SUCCESS_requestWithdraw_recordsAndReturnsFormUrl() {
        // given
        final Long userId = 1L;
        final String withdrawFormUrl = "https://example.com/withdraw-form";
        when(operationConfigService.findOperationConfigValue(OperationConfigCategory.WITHDRAW_FORM, "linkUrl"))
            .thenReturn(Optional.of(withdrawFormUrl));

        // when
        UserWithdrawInfo.WithdrawFormResult result = userWithdrawFacade.requestWithdraw(userId);

        // then
        verify(userWithdrawHistoryService, times(1)).recordWithdrawRequest(userId);
        assertThat(result.getWithdrawFormUrl()).isEqualTo(withdrawFormUrl);
    }

    @Test
    @DisplayName("SUCCESS_폼 설정이 없어도 요청은 기록되고 폼 URL은 비어(null) 반환된다")
    void SUCCESS_requestWithdraw_recordsEvenWhenFormConfigMissing() {
        // given
        final Long userId = 1L;
        when(operationConfigService.findOperationConfigValue(OperationConfigCategory.WITHDRAW_FORM, "linkUrl"))
            .thenReturn(Optional.empty());

        // when
        UserWithdrawInfo.WithdrawFormResult result = userWithdrawFacade.requestWithdraw(userId);

        // then
        verify(userWithdrawHistoryService, times(1)).recordWithdrawRequest(userId);
        assertThat(result.getWithdrawFormUrl()).isNull();
    }
}
