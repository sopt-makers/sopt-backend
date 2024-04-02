package org.sopt.app.application.soptamp;

import java.util.List;
import lombok.Builder;
import lombok.Getter;
import lombok.ToString;
import org.sopt.app.domain.entity.Mission;

public class SoptampPointInfo {

    @Getter
    @Builder
    @ToString
    public static class Main {

        private Integer rank;
        private String nickname;
        private Long point;
        private String profileMessage;
    }

    @Getter
    @Builder
    @ToString
    public static class Detail {

        private String nickname;
        private String profileMessage;
        private List<Mission> userMissions;
    }

    @Getter
    @Builder
    @ToString
    public static class Point {
        private Long id;
        private Long generation;
        private Long soptampUserId;
        private Long points;

        public static Point of(Long id, Long generation, Long soptampUserId, Long points) {
            return new Point(id, generation, soptampUserId, points);
        }
    }

    @Getter
    @Builder
    public static class PartRank {
        private String part;
        private Integer rank;
        private Long points;
    }
}
