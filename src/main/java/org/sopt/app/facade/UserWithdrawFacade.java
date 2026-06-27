package org.sopt.app.facade;

import lombok.RequiredArgsConstructor;
import lombok.val;
import org.sopt.app.application.appservice.OperationConfigService;
import org.sopt.app.application.user.UserWithdrawInfo;
import org.sopt.app.common.config.OperationConfigCategory;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserWithdrawFacade {

    private static final String WITHDRAW_FORM_URL_KEY = "linkUrl";

    private final OperationConfigService operationConfigService;

    public UserWithdrawInfo.WithdrawFormResult getWithdrawForm() {
        val withdrawFormUrl = operationConfigService.getOperationConfigValue(
            OperationConfigCategory.WITHDRAW_FORM,
            WITHDRAW_FORM_URL_KEY
        );
        return UserWithdrawInfo.WithdrawFormResult.from(withdrawFormUrl);
    }
}
