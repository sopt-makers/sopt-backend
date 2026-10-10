package org.sopt.app.presentation.admin.notification;

import jakarta.validation.constraints.NotBlank;
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

    @Getter
    @NoArgsConstructor(access = AccessLevel.PROTECTED)
    @AllArgsConstructor(access = AccessLevel.PROTECTED)
    public static final class SoptLetterUnwrittenReminder {
        @NotBlank(message = "알림 제목은 필수입니다.")
        private String notificationTitle;

        @NotBlank(message = "알림 내용은 필수입니다.")
        private String notificationContent;

        private String deepLink;

        private String webLink;
    }

}
