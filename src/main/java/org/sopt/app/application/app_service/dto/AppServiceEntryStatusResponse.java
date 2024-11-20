package org.sopt.app.application.app_service.dto;

import lombok.*;

@Builder
@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class AppServiceEntryStatusResponse {

    private final String serviceName;
    private final Boolean displayAlarmBadge;
    private final String alarmBadge;

    public static AppServiceEntryStatusResponse createAppServiceEntryStatus (
            AppServiceInfo appServiceInfo, AppServiceBadgeInfo badgeInfo
    ) {
        if(badgeInfo.getDisplayAlarmBadge().equals(Boolean.TRUE)){
            return createAppServiceEntryStatusByDisPlayAlarmBadge(appServiceInfo, badgeInfo.getAlarmBadge());
        }
        return createDefaultAppServiceEntryStatus(appServiceInfo);
    }

    private static AppServiceEntryStatusResponse createAppServiceEntryStatusByDisPlayAlarmBadge(
            AppServiceInfo appServiceInfo, String alarmBadge
    ) {
        return AppServiceEntryStatusResponse.builder()
                .serviceName(appServiceInfo.getServiceName())
                .displayAlarmBadge(true)
                .alarmBadge(alarmBadge)
                .build();
    }

    private static AppServiceEntryStatusResponse createDefaultAppServiceEntryStatus(
            AppServiceInfo appServiceInfo
    ) {
        return AppServiceEntryStatusResponse.builder()
                .serviceName(appServiceInfo.getServiceName())
                .displayAlarmBadge(false)
                .alarmBadge("")
                .build();
    }
}
