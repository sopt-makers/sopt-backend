package org.sopt.app.presentation.rank;

import java.util.List;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.sopt.app.domain.entity.Mission;

public class RankResponse {

    @Getter
    @Setter
    @ToString
    public static class Main {

        private Integer rank;
        private String nickname;
        private Long point;
        private String profileMessage;
    }

    @Getter
    @Setter
    @ToString
    public static class Detail {

        private String nickname;
        private String profileMessage;
        private List<Mission> userMissions;
    }
}
