package org.sopt.app.presentation.notification;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class PushTokenResponse {

    @Getter
    @Builder
    @ToString
    @NoArgsConstructor
    @AllArgsConstructor
    public static class StatusResponse {

        @Schema(description = "알림 서버 Response Status", example = "200")
        private Integer status;

        @Schema(description = "성공 여부", example = "true")
        private Boolean success;

        @Schema(description = "알림 서버 Response Message", example = "토큰 해지 성공")
        private String message;
    }
}
