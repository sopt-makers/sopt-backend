package org.sopt.app.facade;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.sopt.app.application.appservice.OperationConfigService;
import org.sopt.app.application.user.UserWithdrawInfo;
import org.sopt.app.common.config.OperationConfigCategory;
import org.sopt.app.common.exception.NotFoundException;
import org.sopt.app.common.response.ErrorCode;

@ExtendWith(MockitoExtension.class)
@DisplayName("UserWithdrawFacade 단위 테스트")
class UserWithdrawFacadeTest {

    @Mock
    private OperationConfigService operationConfigService;

    @InjectMocks
    private UserWithdrawFacade userWithdrawFacade;

    @Test
    @DisplayName("SUCCESS_운영 설정에서 탈퇴 폼 URL을 조회한다")
    void SUCCESS_getWithdrawForm() {
        // given
        final String withdrawFormUrl = "https://example.com/withdraw-form";
        when(operationConfigService.getOperationConfigValue(OperationConfigCategory.WITHDRAW_FORM, "linkUrl"))
            .thenReturn(withdrawFormUrl);

        // when
        UserWithdrawInfo.WithdrawFormResult result = userWithdrawFacade.getWithdrawForm();

        // then
        assertThat(result.getWithdrawFormUrl()).isEqualTo(withdrawFormUrl);
        verify(operationConfigService, times(1))
            .getOperationConfigValue(OperationConfigCategory.WITHDRAW_FORM, "linkUrl");
    }

    @Test
    @DisplayName("FAIL_탈퇴 폼 운영 설정이 존재하지 않으면 NotFoundException이 전파된다")
    void FAIL_getWithdrawForm_whenConfigNotFound() {
        // given
        when(operationConfigService.getOperationConfigValue(OperationConfigCategory.WITHDRAW_FORM, "linkUrl"))
            .thenThrow(new NotFoundException(ErrorCode.ENTITY_NOT_FOUND));

        // when & then
        assertThatThrownBy(() -> userWithdrawFacade.getWithdrawForm())
            .isInstanceOf(NotFoundException.class)
            .satisfies(e -> {
                NotFoundException exception = (NotFoundException) e;
                assertThat(exception.getErrorCode()).isEqualTo(ErrorCode.ENTITY_NOT_FOUND);
            });
    }
}
