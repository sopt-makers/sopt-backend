package org.sopt.app.application.appservice;

import lombok.RequiredArgsConstructor;
import org.sopt.app.application.appservice.dto.AppServiceBadgeInfo;
import org.sopt.app.application.fortune.FortuneService;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Qualifier("fortuneBadgeManager")
public class FortuneBadgeManager implements AppServiceBadgeManager {

    private final FortuneService fortuneService;

    @Override
    public AppServiceBadgeInfo acquireAppServiceBadgeInfo(final Long userId) {
        if(fortuneService.isExistTodayFortune(userId)){
            return AppServiceBadgeInfo.createWithAllDisabled();
        }
        return AppServiceBadgeInfo.createWithEnabledDisPlayAlarmBadge("N");
    }
}
