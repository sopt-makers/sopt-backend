package org.sopt.app.presentation.poke;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import javax.validation.constraints.NotNull;
import java.util.List;

public class PokeRequest {

    @Getter
    @NoArgsConstructor(access = AccessLevel.PRIVATE)
    @AllArgsConstructor(access = AccessLevel.PRIVATE)
    @ToString
    public static class PokeMessageRequest {
        @Schema(description = "찌르기 메시지 아이디", example = "1")
        @NotNull(message = "messageId may not be null")
        private Long messageId;

    }

    @Getter
    @NoArgsConstructor(access = AccessLevel.PRIVATE)
    @AllArgsConstructor(access = AccessLevel.PRIVATE)
    @ToString
    public static class PokeAlarmRequest {
        @Schema(description = "유저 아이디", example = "['1']")
        @NotNull(message = "target user Id may not be null")
        private List<String> userIds;

        @Schema(description = "알림 제목", example = "['1']")
        @NotNull(message = "Alarm Title may not be null")
        private String title;

        @Schema(description = "알림 내용", example = "['1']")
        @NotNull(message = "Alarm Content may not be null")
        private String content;

        @Schema(description = "알림 카테고리", example = "NEWS")
        @NotNull(message = "Alarm Category may not be null")
        private String category;

        @Schema(description = "찌르기 알림 딥링크", example = "/home/poke")
        private String deepLink;

        public static PokeRequest.PokeAlarmRequest of(List<String> userIds, String title, String content, String category, String deepLink) {
            return new PokeRequest.PokeAlarmRequest(userIds, title, content, category, deepLink);
        }
    }
}
