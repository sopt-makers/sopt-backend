package org.sopt.app.presentation.notification;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;

import jakarta.validation.constraints.NotNull;

import lombok.*;
import org.sopt.app.domain.entity.Notification;
import org.sopt.app.domain.enums.NotificationCategory;
import org.sopt.app.domain.enums.NotificationType;

import java.util.List;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class NotificationRequest {

    @Getter
    @AllArgsConstructor(access = AccessLevel.PROTECTED)
    @Builder
    public static class RegisterNotificationRequest {

        @Schema(description = "알림 대상 유저 플랫폼 ID 리스트", example = "['1', '2']")
        @JsonProperty(value = "userIds")
        private List<String> userIds;

        @Schema(description = "알림 제목", example = "앱팀 최고")
        @NotNull(message = "title may not be null")
        @JsonProperty(value = "title")
        private String title;

        @Schema(description = "알림 제목", example = "앱팀 최고")
        @JsonProperty(value = "content")
        private String content;

        @Schema(description = "알림 범위 타입", example = "SEND")
        @NotNull(message = "type may not be null")
        @JsonProperty(value = "type")
        private NotificationType type;

        @Schema(description = "알림 카테고리", example = "NOTICE")
        @NotNull(message = "category may not be null")
        @JsonProperty(value = "category")
        private NotificationCategory category;

        @Schema(description = "알림 첨부 딥링크")
        @JsonProperty(value = "deepLink")
        private String deepLink;

        @Schema(description = "알림 첨부 웹링크")
        @JsonProperty(value = "webLink")
        private String webLink;

        @Schema(description = "알림 고유값")
        @JsonProperty(value = "id")
        private String notificationId;

        public Notification toEntity(RegisterNotificationRequest request, Long userId) {
            return Notification.builder()
                    .userId(userId)
                    .title(request.title)
                    .content(request.content)
                    .type(request.type)
                    .category(request.category)
                    .deepLink(request.deepLink)
                    .webLink(request.webLink)
                    .notificationId(request.notificationId)
                    .isRead(false)
                    .build();
        }
    }
}
