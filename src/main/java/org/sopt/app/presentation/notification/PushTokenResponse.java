package org.sopt.app.presentation.notification;

import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

public class PushTokenResponse {

    @Getter
    @Builder
    @ToString
    public static class StatusResponse {

        private Integer status;

        private Boolean success;

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
