package org.sopt.app.application.app_service;

import lombok.RequiredArgsConstructor;
import org.sopt.app.application.app_service.dto.AppServiceBadgeInfo;
import org.sopt.app.application.soptamp.SoptampUserService;
import org.sopt.app.domain.enums.Part;
import org.sopt.app.domain.enums.PlaygroundPart;
import org.sopt.app.facade.RankFacade;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Qualifier("soptampBadgeManager")
public class SoptampBadgeManager implements AppServiceBadgeManager {

    private final SoptampUserService soptampUserService;
    private final RankFacade rankFacade;

    @Override
    public AppServiceBadgeInfo acquireAppServiceBadgeInfo(final Long userId) {
        Part part = PlaygroundPart.toPart(soptampUserService.getSoptampUserInfo(userId).getPart());
        if(part == null) {
            return AppServiceBadgeInfo.createWithAllDisabled();
        }
        return AppServiceBadgeInfo.createWithEnabledDisPlayAlarmBadge(
                rankFacade.findPartRank(part).getRank().toString() + "위"
        );
    }
}
