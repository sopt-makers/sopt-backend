package org.sopt.app.presentation.config;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

public class ConfigResponse {

    @Getter
    @Builder
    @ToString
    public static class Availability {

        @Schema(description = "앱 메인 뷰 분기 처리", example = "true")
        private Boolean isAvailable;

    }
}
