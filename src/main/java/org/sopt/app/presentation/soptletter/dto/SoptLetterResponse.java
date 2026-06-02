package org.sopt.app.presentation.soptletter.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class SoptLetterResponse {

    @Schema(description = "솝레터 닉네임")
    public record GeneratedNicknameResponse(
        @Schema(description = "닉네임", example = "익명의 달달한 간장게장")
        String nickname,
        @Schema(description = "온보딩 완료 여부", example = "false")
        boolean isOnboarded
    ) {
    }
}
