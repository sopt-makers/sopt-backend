package org.sopt.app.presentation.stamp;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class StampRequest {

    @Getter
    @AllArgsConstructor(access = AccessLevel.PUBLIC)
    @NoArgsConstructor(access = AccessLevel.PRIVATE)
    public static class FindStampRequest {

        @Schema(description = "미션 아이디", example = "1")
        @NotNull(message = "missionId may not be null")
        private Long missionId;
        @Schema(description = "닉네임", example = "스탬프왕")
        @NotNull(message = "nickname may not be null")
        private String nickname;
    }

    @Getter
    @NoArgsConstructor(access = AccessLevel.PRIVATE)
    public static class RegisterStampRequest extends BaseStampRequest{

        public RegisterStampRequest(Long missionId, String image, String contents, String activityDate) {
            super(missionId, image, contents, activityDate);
        }
    }

    @Getter
    @NoArgsConstructor(access = AccessLevel.PRIVATE)
    public static class EditStampRequest extends BaseStampRequest{

        public EditStampRequest(Long missionId, String image, String contents, String activityDate) {
            super(missionId, image, contents, activityDate);
        }
    }

    @Getter
    @AllArgsConstructor
    @NoArgsConstructor(access = AccessLevel.PROTECTED)
    public static abstract class BaseStampRequest {
        @Schema(description = "미션 아이디", example = "1")
        @NotNull(message = "missionId may not be null")
        private Long missionId;

        @Schema(description = "스탬프 이미지", example = "https://s3.ap-northeast-2.amazonaws.com/example/283aab53-22e3-46da-85ec-146c99f82ed4")
        @NotNull(message = "image may not be null")
        private String image;

        @Schema(description = "스탬프 내용", example = "스탬프 찍었다!")
        @NotNull(message = "contents may not be null")
        @NotEmpty(message = "contents may not be empty")
        private String contents;

        @Schema(description = "활동 날짜", example = "2024.04.08")
        @NotNull(message = "activity date may not be null")
        private String activityDate;

    }
}
