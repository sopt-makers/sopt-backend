package org.sopt.app.presentation.stamp;

import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDateTime;
import javax.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

public class StampRequest {

    @Getter
    @Setter
    @ToString
    public static class FindStampRequest {

        @Schema(description = "미션 아이디", example = "1")
        @NotNull(message = "missionId may not be null")
        private Long missionId;
        @Schema(description = "닉네임", example = "스탬프왕")
        @NotNull(message = "nickname may not be null")
        private String nickname;
    }

    @Getter
    @Setter
    @ToString
    public static class RegisterStampRequest {

        @Schema(description = "미션 아이디", example = "1")
        @NotNull(message = "missionId may not be null")
        private Long missionId;
        @Schema(description = "스탬프 이미지", example = "https://s3.ap-northeast-2.amazonaws.com/example/283aab53-22e3-46da-85ec-146c99f82ed4.jpeg")
        @NotNull(message = "image may not be null")
        private String image;
        @Schema(description = "스탬프 내용", example = "스탬프 찍었다!")
        @NotNull(message = "contents may not be null")
        private String contents;
        @Schema(description = "활동 날짜", example = "2023-03-29T18:39:42.106369")
        @NotNull(message = "activity date may not be null")
        private LocalDateTime activityDate;
    }

    @Getter
    @Setter
    @ToString
    public static class EditStampRequest {

        @Schema(description = "미션 아이디", example = "1")
        @NotNull(message = "missionId may not be null")
        private Long missionId;
        @Schema(description = "스탬프 이미지", example = "https://s3.ap-northeast-2.amazonaws.com/example/283aab53-22e3-46da-85ec-146c99f82ed4")
        @NotNull(message = "image may not be null")
        private String image;
        @Schema(description = "스탬프 내용", example = "스탬프 찍었다!")
        @NotNull(message = "contents may not be null")
        private String contents;
        @Schema(description = "활동 날짜", example = "2023-03-29T18:39:42.106369")
        @NotNull(message = "activity date may not be null")
        private LocalDateTime activityDate;
    }
}
