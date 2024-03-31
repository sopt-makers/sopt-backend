package org.sopt.app.presentation.stamp;

import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDateTime;
import java.util.List;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

public class StampResponse {

    @Getter
    @Setter
    @ToString
    public static class StampMain {

        @Schema(description = "스탬프 아이디", example = "1")
        private Long id;
        @Schema(description = "스탬프 내용", example = "모각공했다!")
        private String contents;
        @Schema(description = "스탬프 이미지", example = "[https://s3.ap-northeast-2.amazonaws.com/example/283aab53-22e3-46da-85ec-146c99f82ed4.jpeg]")
        private List<String> images;
        @Schema(description = "활동 날짜", example = "2023-03-29T18:39:42.106369")
        private LocalDateTime activityDate;
        @Schema(description = "스탬프 생성 일시", example = "2023-03-29T18:39:42.106369")
        private LocalDateTime createdAt;
        @Schema(description = "스탬프 수정 일시", example = "2023-03-29T18:39:42.106369")
        private LocalDateTime updatedAt;
        @Schema(description = "미션 아이디", example = "3")
        private Long missionId;
    }

    @Getter
    @Setter
    @ToString
    public static class StampId {

        @Schema(description = "스탬프 아이디", example = "1")
        private Long stampId;
    }
}
