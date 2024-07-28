package org.sopt.app.presentation.description;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class DescriptionResponse {

    @Getter
    @NoArgsConstructor(access = AccessLevel.PRIVATE)
    @AllArgsConstructor(access = AccessLevel.PUBLIC)
    @Builder
    public static class MainDescription {
        @Schema(description="메인 상단 문구", example = "33기 Do sopt에 오신 것을 환영합니다!")
        private String topDescription;
        @Schema(description="메인 하단 문구", example = "벋벋조와 어쩌구")
        private String bottomDescription;
    }
}
