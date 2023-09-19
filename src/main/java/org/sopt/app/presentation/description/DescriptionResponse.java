package org.sopt.app.presentation.description;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

public class DescriptionResponse {

    @Getter
    @Setter
    @ToString
    @Builder
    public static class MainDescription {
        @Schema(description="메인 상단 문구", example = "33기 Do sopt에 오신 것을 환영합니다!")
        private String topDescription;
        @Schema(description="메인 하단 문구", example = "벋벋조와 어쩌구")
        private String bottomDescription;
    }
}
