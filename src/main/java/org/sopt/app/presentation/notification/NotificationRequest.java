package org.sopt.app.presentation.notification;

import io.swagger.v3.oas.annotations.media.Schema;
import javax.validation.constraints.NotNull;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.List;

public class NotificationRequest {

    @Getter
    @Setter
    @ToString
    public static class RegisterNotificationRequest {

        @Schema(description = "알림 제목", example = "앱팀 최고")
        @NotNull(message = "title may not be null")
        private String title;
        @Schema(description = "알림 내용", example = "알림 내용은 이러쿵저러쿵 어쩌구저쩌구입니다.")
        @NotNull(message = "content may not be null")
        private String content;
//        @Schema(description = "알림 타입", example = "NEWS")
//        @NotNull(message = "type may not be null")
//        private NotificationType type;
    }

    @Getter
    @Builder
    @ToString
    public static class SendRequestNoLink {
        private List<String> userIds;
        private String title;
        private String content;
    }

    @Getter
    @Builder
    @ToString
    public static class SendRequestWithDeepLink {
        private List<String> userIds;
        private String title;
        private String content;
        private String deepLink;
    }

    @Getter
    @Builder
    @ToString
    public static class SendRequestWithWebLink {
        private List<String> userIds;
        private String title;
        private String content;
        private String webLink;
    }

    @Getter
    @Builder
    @ToString
    public static class SendRequestWithAllLink {
        private List<String> userIds;
        private String title;
        private String content;
        private String deepLink;
        private String webLink;
    }

}
