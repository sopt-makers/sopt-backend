package org.sopt.app.presentation.notification;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

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

    /**
     * Register & Update & Delete API 에 대해서 모든 Response 형식 동일
     */
//    @Getter
//    @Builder
//    @ToString
//    public static class TokenResponse {
//
//        private Long userId;
//
//        private String pushToken;
//    }
}
