package org.sopt.app.application.app_service.dto;

import lombok.*;

@Builder
@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class AppServiceBadgeInfo {

    private Boolean displayAlarmBadge;
    private String alarmBadge;

    public static AppServiceBadgeInfo createWithEnabledDisPlayAlarmBadge(String alarmBadge) {
        return AppServiceBadgeInfo.builder()
                .displayAlarmBadge(true)
                .alarmBadge(alarmBadge)
                .build();
    }

    public static AppServiceBadgeInfo createWithAllDisabled() {
        return AppServiceBadgeInfo.builder()
                .displayAlarmBadge(false)
                .alarmBadge("")
                .build();
    }
}
