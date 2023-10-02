package org.sopt.app.presentation.notification;

import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDateTime;

import lombok.*;

public class NotificationResponse {

    @Getter
    @NoArgsConstructor(access = AccessLevel.PRIVATE)
    @AllArgsConstructor(access = AccessLevel.PRIVATE)
    @ToString
    public static class NotificationSimple {

        @Schema(description = "알림 아이디", example = "1")
        private Long notificationId;

        @Schema(description = "앱 유저 아이디", example = "1")
        private Long userId;

        @Schema(description = "알림 제목", example = "공지다!")
        private String title;

        @Schema(description = "알림 내용", example = "공지 내용은 앱팀 최고입니다.")
        private String content;

        @Schema(description = "알림 카테고리", example = "공지 내용은 앱팀 최고입니다.")
        private String category;

        @Schema(description = "알림 읽음 여부", example = "true")
        private Boolean isRead;

        @Schema(description = "알림 생성 일시", example = "2023-03-29T18:39:42.106369")
        private LocalDateTime createdAt;

        public static NotificationSimple of(
                Long notificationId
                , Long userId
                , String title
                , String content
                , String category
                , Boolean isRead
                , LocalDateTime createdAt
        ) {
            return new NotificationSimple(
                    notificationId, userId, title, content, category, isRead, createdAt
            );
        }
    }

    @Getter
    @NoArgsConstructor(access = AccessLevel.PRIVATE)
    @AllArgsConstructor(access = AccessLevel.PRIVATE)
    @ToString
    public static class NotificationDetail {

        @Schema(description = "알림 아이디", example = "1")
        private Long notificationId;
        @Schema(description = "유저 유저 아이디", example = "1")
        private Long userId;
        @Schema(description = "알림 제목", example = "공지다!")
        private String title;
        @Schema(description = "알림 내용", example = "공지 내용은 앱팀 최고입니다.")
        private String content;
        @Schema(description = "알림 첨부 딥링크")
        private String deepLink;
        @Schema(description = "알림 첨부 웹링크")
        private String webLink;
        @Schema(description = "알림 생성 일시", example = "2023-03-29T18:39:42.106369")
        private LocalDateTime createdAt;
        @Schema(description = "알림 수정 일시", example = "2023-03-29T18:39:42.106369")
        private LocalDateTime updatedAt;

        public static NotificationDetail of(
                Long notificationId
                , Long userId
                , String title
                , String content
                , String deepLink
                , String webLink
                , LocalDateTime createdAt
                , LocalDateTime updatedAt
        ) {
            return new NotificationDetail(
                    notificationId, userId, title, content, deepLink, webLink, createdAt, updatedAt
            );
        }
    }

    @Getter
    @NoArgsConstructor(access = AccessLevel.PRIVATE)
    @AllArgsConstructor(access = AccessLevel.PRIVATE)
    @ToString
    public static class NotificationConfirmStatus {
        @Schema(description = "알림 전체 읽음 여부", example = "false")
        private Boolean isAllConfirm;

        public static NotificationConfirmStatus of(
                Boolean isAllConfirm
        ) {
            return new NotificationConfirmStatus(
                    isAllConfirm
            );
        }
    }
}
