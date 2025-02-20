package org.sopt.app.application.app_service;

import lombok.RequiredArgsConstructor;
import org.sopt.app.application.app_service.dto.AppServiceBadgeInfo;
import org.sopt.app.application.poke.PokeHistoryService;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Qualifier("pokeBadgeManager")
public class PokeBadgeManager implements AppServiceBadgeManager {

    private final PokeHistoryService pokeHistoryService;

    @Override
    public AppServiceBadgeInfo acquireAppServiceBadgeInfo(final Long userId) {
        Long unRepliedPokeMeSize = pokeHistoryService.getUnRepliedPokeMeSize(userId);
        if(unRepliedPokeMeSize > 0) {
            return AppServiceBadgeInfo.createWithEnabledDisPlayAlarmBadge(limitLessThanTwoNumbers(unRepliedPokeMeSize));
        }
        return AppServiceBadgeInfo.createWithAllDisabled();
    }

    private String limitLessThanTwoNumbers(final Long number) {
        return number > 9 ? "9+" : number.toString();
    }
}
