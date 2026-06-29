package org.sopt.app.facade;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.sopt.app.application.appservice.OperationConfigService;
import org.sopt.app.application.user.UserWithdrawInfo;
import org.sopt.app.application.user.UserWithdrawHistoryService;
import org.sopt.app.common.config.OperationConfigCategory;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserWithdrawFacade {

    private static final String WITHDRAW_FORM_URL_KEY = "linkUrl";

    private final OperationConfigService operationConfigService;
    private final UserWithdrawHistoryService userWithdrawHistoryService;

    public UserWithdrawInfo.WithdrawFormResult requestWithdraw(Long userId) {
        userWithdrawHistoryService.recordWithdrawRequest(userId);
        return getWithdrawFormOrEmpty();
    }

    private UserWithdrawInfo.WithdrawFormResult getWithdrawFormOrEmpty() {
        val withdrawFormUrl = operationConfigService.findOperationConfigValue(
            OperationConfigCategory.WITHDRAW_FORM,
            WITHDRAW_FORM_URL_KEY
        ).orElse(null);
        if (withdrawFormUrl == null) {
            log.warn("탈퇴 요청은 기록되었으나 WITHDRAW_FORM- {} 운영 설정이 존재하지 않음.",
                WITHDRAW_FORM_URL_KEY);
        }
        return UserWithdrawInfo.WithdrawFormResult.from(withdrawFormUrl);
    }
}
