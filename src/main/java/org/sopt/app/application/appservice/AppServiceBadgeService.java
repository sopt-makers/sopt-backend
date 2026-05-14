package org.sopt.app.application.appservice;

import static org.sopt.app.common.config.AsyncConfig.CACHE_SYNC_EXECUTOR;

import java.util.Map;
import java.util.concurrent.CompletableFuture;
import lombok.RequiredArgsConstructor;
import org.sopt.app.application.appservice.dto.AppServiceBadgeInfo;
import org.sopt.app.application.appservice.dto.AppServiceEntryStatusResponse;
import org.sopt.app.application.appservice.dto.AppServiceInfo;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AppServiceBadgeService {

    private final Map<String, AppServiceBadgeManager> badgeManagerMap;

    public AppServiceEntryStatusResponse getAppServiceEntryStatusResponse(
            final AppServiceInfo appServiceInfo, final Long userId
    ) {
        AppServiceBadgeManager badgeManager = getAppServiceBadgeManager(appServiceInfo);
        AppServiceBadgeInfo badgeInfo = badgeManager.acquireAppServiceBadgeInfo(userId);
        return AppServiceEntryStatusResponse.createAppServiceEntryStatus(appServiceInfo, badgeInfo);
    }

    @Async(CACHE_SYNC_EXECUTOR)
    public CompletableFuture<AppServiceEntryStatusResponse> getAppServiceEntryStatusResponseAsync(
            final AppServiceInfo appServiceInfo, final Long userId
    ) {
        return CompletableFuture.completedFuture(getAppServiceEntryStatusResponse(appServiceInfo, userId));
    }

    private AppServiceBadgeManager getAppServiceBadgeManager(AppServiceInfo appServiceInfo) {
        AppServiceBadgeManager badgeManager = badgeManagerMap.get(
                AppServiceName.of(appServiceInfo.getServiceName()).getBadgeManagerName()
        );

        if(badgeManager == null) {
            badgeManager = badgeManagerMap.get("defaultBadgeManager");
        }
        return badgeManager;
    }
}
