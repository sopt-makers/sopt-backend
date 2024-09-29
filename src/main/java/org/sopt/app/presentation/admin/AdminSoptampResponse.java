package org.sopt.app.presentation.admin;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class AdminSoptampResponse {

    @Getter
    @Builder
    public static class Rows {

        @Schema(description = "SoptampUser 초기화 성공 회원 수", example = "200")
        private int soptampUserRows;
    }
}
