package org.sopt.app.presentation.notification;

import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDateTime;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

public class NotificationResponse {

    @Getter
    @Setter
    @ToString
    public static class NotificationMain {

        @Schema(description = "알림 아이디", example = "1")
        private Long id;
        @Schema(description = "유저 아이디", example = "1")
        private Long userId;
        @Schema(description = "알림 제목", example = "공지다!")
        private String title;
        @Schema(description = "알림 내용", example = "공지 내용은 앱팀 최고입니다.")
        private String content;
        @Schema(description = "알림 타입", example = "NEWS")
        private String type;
        @Schema(description = "알림 읽음 여부", example = "true")
        private Boolean isRead;
        @Schema(description = "알림 생성 일시", example = "2023-03-29T18:39:42.106369")
        private LocalDateTime createdAt;
        @Schema(description = "알림 수정 일시", example = "2023-03-29T18:39:42.106369")
        private LocalDateTime updatedAt;

    }

    @Getter
    @Setter
    @ToString
    public static class NotificationIsRead {

        @Schema(description = "알림 아이디", example = "1")
        private Long id;
        @Schema(description = "알림 읽음 여부", example = "true")
        private Boolean isRead;
        @Schema(description = "알림 생성 일시", example = "2023-03-29T18:39:42.106369")
        private LocalDateTime createdAt;
        @Schema(description = "알림 수정 일시", example = "2023-03-29T18:39:42.106369")
        private LocalDateTime updatedAt;

    }

    @Getter
    @Setter
    @ToString
    public static class NotificationMainView {

        @Schema(description = "알림 전체 읽음 여부", example = "false")
        private Boolean exists;

    }
}
