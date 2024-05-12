package org.sopt.app.presentation.admin;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

public class AdminSoptampResponse {

    @Getter
    @Setter
    @ToString
    @Builder
    public static class Rows {

        @Schema(description = "SoptampUser 초기화 성공 회원 수", example = "200")
        private int soptampUserRows;


        @Schema(description = "SoptampPoint 초기화 성공 회원 수", example = "200")
        private int soptampPointRows;
    }
}
