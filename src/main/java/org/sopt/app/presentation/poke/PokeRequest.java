package org.sopt.app.presentation.poke;

import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class PokeRequest {

    @Getter
    @NoArgsConstructor(access = AccessLevel.PRIVATE)
    @AllArgsConstructor(access = AccessLevel.PRIVATE)
    @ToString
    public static class PokeMessageRequest {

        @Schema(description = "찌르기 메시지", example = "Message A")
        @NotNull(message = "messageId may not be null")
        private String message;
        @Schema(description = "익명 여부", example = "true")
        private Boolean isAnonymous = false;
    }

    @Getter
    @Builder(access = AccessLevel.PRIVATE)
    @AllArgsConstructor(access = AccessLevel.PRIVATE)
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

        public static PokeAlarmRequest of(Long userId) {
            return PokeAlarmRequest.builder()
                    .userIds(List.of(String.valueOf(userId)))
                    .title("콕 찌르기")
                    .content("누군가가 콕 찔렀어요. 확인해보세요!")
                    .category("NEWS")
                    .deepLink("home/poke/notification-list")
                    .build();
        }
    }
}
