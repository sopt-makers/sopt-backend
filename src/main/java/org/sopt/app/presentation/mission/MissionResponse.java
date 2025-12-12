package org.sopt.app.presentation.mission;


import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class MissionResponse {

    @Getter
    @NoArgsConstructor(access = AccessLevel.PRIVATE)
    @AllArgsConstructor(access = AccessLevel.PUBLIC)
    public static class MissionMain {

        @Schema(description = "미션 아이디", example = "1")
        private Long id;
        @Schema(description = "미션 제목", example = "팀원 칭찬하기")
        private String title;
        @Schema(description = "미션 레벨", example = "1")
        private Integer level;
        @Schema(description = "미션 프로필 이미지", example = "null")
        private List<String> profileImage;
    }

    @Getter
    @NoArgsConstructor(access = AccessLevel.PRIVATE)
    @AllArgsConstructor(access = AccessLevel.PUBLIC)
    public static class Completeness {

        @Schema(description = "미션 아이디", example = "1")
        private Long id;
        @Schema(description = "미션 제목", example = "팀원 칭찬하기")
        private String title;
        @Schema(description = "미션 레벨", example = "1")
        private Integer level;
        @Schema(description = "미션 프로필 이미지", example = "null")
        private List<String> profileImage;
        @Schema(description = "미션 완료 여부", example = "true")
        private Boolean isCompleted;
    }

    @Getter
    @NoArgsConstructor(access = AccessLevel.PRIVATE)
    @AllArgsConstructor(access = AccessLevel.PUBLIC)
    public static class MissionId {

        @Schema(description = "미션 아이디", example = "1")
        private Long missionId;
    }

}