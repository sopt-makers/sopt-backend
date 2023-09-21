package org.sopt.app.presentation.notification;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

public class OptionRequest {
    @Getter
    @Setter
    @ToString
    public static class EditOptInRequest {

        @Schema(description = "전체 알림 수신 동의", example = "false")
        private Boolean allOptIn;

        @Schema(description = "파트별 알림 수신 동의", example = "true")
        private Boolean partOptIn;

        @Schema(description = "소식 알림 수신 동의", example = "false")
        private Boolean newsOptIn;
    }
}
