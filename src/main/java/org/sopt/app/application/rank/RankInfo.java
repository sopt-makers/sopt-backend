package org.sopt.app.application.rank;

import java.util.List;
import lombok.Builder;
import lombok.Getter;
import lombok.ToString;
import org.sopt.app.domain.entity.Mission;

public class RankInfo {

    @Getter
    @Builder
    @ToString
    public static class Main {

        private Integer rank;
        private Long userId;
        private String nickname;
        private Long point;
        private String profileMessage;
    }

    @Getter
    @Builder
    @ToString
    public static class Detail {

        private Long userId;
        private String nickname;
        private String profileMessage;
        private List<Mission> userMissions;
    }

    @Getter
    @Builder
    @ToString
    public static class Profile {

    }
}
