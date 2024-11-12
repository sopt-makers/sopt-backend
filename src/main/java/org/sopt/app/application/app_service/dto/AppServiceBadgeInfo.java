package org.sopt.app.application.app_service.dto;

import lombok.*;

@Builder
@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class AppServiceBadgeInfo {

    private Boolean displayMessage;
    private Boolean displayAlarmBadge;
    private String alarmBadge;

    public static AppServiceBadgeInfo createWithEnabledDisPlayAlarmBadge(String alarmBadge) {
        return AppServiceBadgeInfo.builder()
                .displayMessage(false)
                .displayAlarmBadge(true)
                .alarmBadge(alarmBadge)
                .build();
    }

    public static AppServiceBadgeInfo createWithEnabledDisPlayMessage() {
        return AppServiceBadgeInfo.builder()
                .displayMessage(true)
                .displayAlarmBadge(false)
                .alarmBadge("")
                .build();
    }

    public static AppServiceBadgeInfo createWithAllDisabled() {
        return AppServiceBadgeInfo.builder()
                .displayMessage(false)
                .displayAlarmBadge(false)
                .alarmBadge("")
                .build();
    }
}
