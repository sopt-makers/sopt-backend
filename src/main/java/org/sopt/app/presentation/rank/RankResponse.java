package org.sopt.app.presentation.rank;

import java.util.List;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.sopt.app.domain.entity.Mission;
import org.sopt.app.domain.enums.OsType;

public class RankResponse {

    @Getter
    @Setter
    @ToString
    public static class Main {

        private Integer rank;
        private Long userId;
        private String nickname;
        private Long point;
        private String profileMessage;
    }

    @Getter
    @Setter
    @ToString
    public static class Detail {

        private Long userId;
        private String nickname;
        private String profileMessage;
        private List<Mission> userMissions;
    }

    @Getter
    @Setter
    @ToString
    public static class Profile {

        private Long id;
        private String nickname;
        private String email;
        private String password;
        private String clientToken;
        private String profileMessage;
        private Long points;
        private OsType osType;
    }
}
