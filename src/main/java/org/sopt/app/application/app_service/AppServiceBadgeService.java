package org.sopt.app.application.app_service;

import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.sopt.app.application.app_service.dto.AppServiceBadgeInfo;
import org.sopt.app.application.app_service.dto.AppServiceEntryStatusResponse;
import org.sopt.app.application.app_service.dto.AppServiceInfo;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AppServiceBadgeService {

    private final Map<String, AppServiceBadgeManager> badgeManagerMap;

    public AppServiceEntryStatusResponse getAppServiceEntryStatusResponse(
            final AppServiceInfo appServiceInfo, final Long userId
    ) {
        AppServiceBadgeManager badgeManager = badgeManagerMap.get(
                AppServiceName.of(appServiceInfo.getServiceName()).getBadgeManagerName()
        );

        if(badgeManager == null) {
            badgeManager = badgeManagerMap.get("defaultBadgeManager");
        }

        AppServiceBadgeInfo badgeInfo = badgeManager.acquireAppServiceBadgeInfo(userId);
        return AppServiceEntryStatusResponse.createAppServiceEntryStatus(appServiceInfo, badgeInfo);
    }
}
