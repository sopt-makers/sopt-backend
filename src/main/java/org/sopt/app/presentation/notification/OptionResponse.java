package org.sopt.app.presentation.notification;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

public class OptionResponse {
    @Getter
    @Builder
    @ToString
    public static class OptIn {

        @Schema(description = "전체 알림 수신 동의", example = "false")
        private Boolean allOptIn;

        @Schema(description = "파트별 알림 수신 동의", example = "true")
        private Boolean partOptIn;

        @Schema(description = "소식 알림 수신 동의", example = "false")
        private Boolean newsOptIn;
    }
}
