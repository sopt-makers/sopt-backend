package org.sopt.app.application.app_service.dto;

import lombok.*;
import org.sopt.app.application.app_service.AppServiceName;

@Builder
@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class AppServiceEntryStatusResponse {

    private final String serviceName;
    private final Boolean displayAlarmBadge;
    private final String alarmBadge;
    private final String iconUrl;
    private final String deepLink;

    public static AppServiceEntryStatusResponse createAppServiceEntryStatus (
            AppServiceInfo appServiceInfo, AppServiceBadgeInfo badgeInfo
    ) {
        return AppServiceEntryStatusResponse.builder()
                .serviceName(AppServiceName.of(appServiceInfo.getServiceName()).getExposedName())
                .displayAlarmBadge(badgeInfo.getDisplayAlarmBadge())
                .alarmBadge(badgeInfo.getAlarmBadge())
                .iconUrl(appServiceInfo.getIconUrl())
                .deepLink(appServiceInfo.getDeepLink())
                .build();
    }
}
