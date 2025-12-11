package org.sopt.app.presentation.appjamtamp;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class AppjamtampRequest {

    @Getter
    @AllArgsConstructor
    @NoArgsConstructor(access = AccessLevel.PRIVATE)
    public static class FindStampRequest {

        @Schema(description = "미션 아이디", example = "1")
        @NotNull(message = "missionId may not be null")
        private Long missionId;
        @Schema(description = "닉네임", example = "보핏아무개")
        @NotNull(message = "nickname may not be null")
        private String nickname;
    }

}
