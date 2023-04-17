package org.sopt.app.presentation.rank;

import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

public class RankResponse {

    @Getter
    @Setter
    @ToString
    public static class RankMain {

        @Schema(description = "유저 랭킹 순위", example = "1")
        private Integer rank;
        @Schema(description = "유저 닉네임", example = "김앱짱")
        private String nickname;
        @Schema(description = "유저 랭킹 점수", example = "15")
        private Long point;
        @Schema(description = "유저 프로필 메세지", example = "1등이 되고 말거야!")
        private String profileMessage;
    }

    @Getter
    @Setter
    @ToString
    public static class Detail {

        @Schema(description = "유저 닉네임", example = "김앱짱")
        private String nickname;
        @Schema(description = "유저 프로필 메세지", example = "1등이 되고 말거야!")
        private String profileMessage;
        @Schema(description = "유저 미션 리스트", example = "")
        private List<RankMission> userMissions;
    }

    @Getter
    @Setter
    @ToString
    public static class RankMission {

        @Schema(description = "미션 아이디", example = "1")
        private Long id;
        @Schema(description = "미션 제목", example = "팀원 칭찬하기")
        private String title;
        @Schema(description = "미션 레벨", example = "1")
        private Integer level;
        @Schema(description = "미션 노출 여부", example = "true")
        private Boolean display;
        @Schema(description = "미션 프로필 이미지", example = "null")
        private List<String> profileImage;
    }
}
