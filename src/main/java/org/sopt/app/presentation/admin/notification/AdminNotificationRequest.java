package org.sopt.app.presentation.admin.notification;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class AdminNotificationRequest {

    @Getter
    @NoArgsConstructor(access = AccessLevel.PROTECTED)
    @AllArgsConstructor(access = AccessLevel.PROTECTED)
    public static final class SoptampShowcase  {
        private String nickname;
        private Long missionId;
        private String notificationTitle;
        private String notificationContent;
    }

}
