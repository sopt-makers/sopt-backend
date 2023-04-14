package org.sopt.app.presentation.rank;

import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.sopt.app.domain.entity.Mission;

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
        @Schema(description = "유저 미션 리스트", example = "[]")
        private List<Mission> userMissions;
    }
}
