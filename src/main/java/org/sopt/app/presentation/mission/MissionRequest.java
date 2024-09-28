package org.sopt.app.presentation.mission;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class MissionRequest {

    @Getter
    @NoArgsConstructor(access = AccessLevel.PRIVATE)
    @AllArgsConstructor(access = AccessLevel.PUBLIC)
    public static class RegisterMissionRequest {

        @Schema(description = "미션 이미지", example = "https://s3.ap-northeast-2.amazonaws.com/example/283aab53-22e3-46da-85ec-146c99f82ed4")
        @NotNull(message = "image may not be null")
        private String image;
        @Schema(description = "미션 제목", example = "앱팀 최고")
        @NotNull(message = "title may not be null")
        private String title;
        @Schema(description = "미션 레벨", example = "3")
        @NotNull(message = "level may not be null")
        private Integer level;
    }
}
