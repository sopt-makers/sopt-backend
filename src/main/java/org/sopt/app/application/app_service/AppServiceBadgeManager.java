package org.sopt.app.application.app_service;

import org.sopt.app.application.app_service.dto.AppServiceBadgeInfo;

public interface AppServiceBadgeManager {

    AppServiceBadgeInfo acquireAppServiceBadgeInfo(final Long userId);
}
