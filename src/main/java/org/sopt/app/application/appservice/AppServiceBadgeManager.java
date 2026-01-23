package org.sopt.app.application.appservice;

import org.sopt.app.application.appservice.dto.AppServiceBadgeInfo;

public interface AppServiceBadgeManager {

    AppServiceBadgeInfo acquireAppServiceBadgeInfo(final Long userId);
}
