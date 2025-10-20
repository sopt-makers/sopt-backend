package org.sopt.app.presentation.stamp;

import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDateTime;
import java.util.List;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class StampResponse {

    @Getter
    @AllArgsConstructor(access = AccessLevel.PUBLIC)
    @NoArgsConstructor(access = AccessLevel.PRIVATE)
    @Builder
    public static class StampMain {

        @Schema(description = "스탬프 아이디", example = "1")
        private Long id;
        @Schema(description = "스탬프 내용", example = "모각공했다!")
        private String contents;
        @Schema(description = "스탬프 이미지", example = "[https://s3.ap-northeast-2.amazonaws.com/example/283aab53-22e3-46da-85ec-146c99f82ed4.jpeg]")
        private List<String> images;
        @Schema(description = "활동 날짜", example = "2024.04.08")
        private String activityDate;
        @Schema(description = "스탬프 생성 일시", example = "2023-03-29T18:39:42.106369")
        private LocalDateTime createdAt;
        @Schema(description = "스탬프 수정 일시", example = "2023-03-29T18:39:42.106369")
        private LocalDateTime updatedAt;
        @Schema(description = "미션 아이디", example = "3")
        private Long missionId;
        @Schema(description = "총 박수 횟수", example = "124")
        private int clapCount;
        @Schema(description = "조회수", example = "58")
        private int viewCount;
    }

    @Getter
    @AllArgsConstructor(access = AccessLevel.PUBLIC)
    @NoArgsConstructor(access = AccessLevel.PRIVATE)
    @Builder
    public static class StampView {

        @Schema(description = "스탬프 아이디", example = "1")
        private Long id;
        @Schema(description = "스탬프 내용", example = "모각공했다!")
        private String contents;
        @Schema(description = "스탬프 이미지", example = "[https://s3.ap-northeast-2.amazonaws.com/example/283aab53-22e3-46da-85ec-146c99f82ed4.jpeg]")
        private List<String> images;
        @Schema(description = "활동 날짜", example = "2024.04.08")
        private String activityDate;
        @Schema(description = "스탬프 생성 일시", example = "2023-03-29T18:39:42.106369")
        private LocalDateTime createdAt;
        @Schema(description = "스탬프 수정 일시", example = "2023-03-29T18:39:42.106369")
        private LocalDateTime updatedAt;
        @Schema(description = "미션 아이디", example = "3")
        private Long missionId;
        @Schema(description = "총 박수 횟수", example = "124")
        private int clapCount;
        @Schema(description = "조회수", example = "58")
        private int viewCount;
        @Schema(description = "내 스탬프인지 여부", example = "false")
        private boolean isMine;
        @Schema(description = "해당 스탬프에 대한 내 박수 횟수", example = "33")
        private int myClapCount;
    }

    @Getter
    @AllArgsConstructor(access = AccessLevel.PUBLIC)
    @NoArgsConstructor(access = AccessLevel.PRIVATE)
    public static class StampId {

        @Schema(description = "스탬프 아이디", example = "1")
        private Long stampId;
    }

    @Getter
    @AllArgsConstructor(access = AccessLevel.PUBLIC)
    public static class SoptampReportResponse{
        private String reportUrl;
    }
}
