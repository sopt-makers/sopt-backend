package org.sopt.app.presentation.appjamtamp;

import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.sopt.app.domain.enums.TeamNumber;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class AppjamtampResponse {

    @Getter
    @NoArgsConstructor(access = AccessLevel.PRIVATE)
    @AllArgsConstructor(access = AccessLevel.PUBLIC)
    public static class AppjamMissionResponse {

        @Schema(description = "미션 아이디", example = "1")
        private Long id;
        @Schema(description = "미션 제목", example = "팀원 칭찬하기")
        private String title;
        @Schema(description = "미션 수행자 닉네임", example = "보핏아무개")
        private String ownerName;
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
    public static class AppjamMissionResponses {

        @Schema(description = "팀 번호", example = "FIRST")
        private TeamNumber teamNumber;
        @Schema(description = "팀 이름", example = "보핏")
        private String teamName;
        @Schema(description = "미션 정보")
        private List<AppjamMissionResponse> missions;
    }

}
