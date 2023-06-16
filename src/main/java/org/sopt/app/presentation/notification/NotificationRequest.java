package org.sopt.app.presentation.notification;

import io.swagger.v3.oas.annotations.media.Schema;
import javax.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

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
    }
}
