package org.sopt.app.application.appservice;

import lombok.RequiredArgsConstructor;
import org.sopt.app.application.appservice.dto.AppServiceBadgeInfo;
import org.sopt.app.application.soptletter.SoptLetterService;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Qualifier("soptLetterBadgeManager")
public class SoptLetterBadgeManager implements AppServiceBadgeManager {

    private final SoptLetterService soptLetterService;

    @Override
    public AppServiceBadgeInfo acquireAppServiceBadgeInfo(final Long userId) {
        long unreadCount = soptLetterService.getTotalUnreadCount(userId);
        if (unreadCount > 0) {
            return AppServiceBadgeInfo.createWithEnabledDisPlayAlarmBadge(limitLessThanThreeNumbers(unreadCount));
        }
        return AppServiceBadgeInfo.createWithAllDisabled();
    }

    private String limitLessThanThreeNumbers(final long number) {
        return number > 99 ? "99+" : String.valueOf(number);
    }
}
