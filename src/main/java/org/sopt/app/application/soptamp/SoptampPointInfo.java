package org.sopt.app.application.soptamp;

import java.util.List;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.sopt.app.domain.entity.Mission;
import org.sopt.app.domain.entity.SoptampPoint;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class SoptampPointInfo {

    @Getter
    @Builder
    @ToString
    public static class Main {

        private Integer rank;
        private String nickname;
        private Long point;
        private String profileMessage;

        public static Main of(Integer rank, SoptampUserInfo soptampUserInfo) {
            return new Main(
                    rank,
                    soptampUserInfo.getNickname(),
                    soptampUserInfo.getTotalPoints(),
                    soptampUserInfo.getProfileMessage()
            );
        }
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

        public static Point of(SoptampPoint soptampPoint) {
            return new Point(
                    soptampPoint.getId(),
                    soptampPoint.getGeneration(),
                    soptampPoint.getSoptampUserId(),
                    soptampPoint.getPoints()
            );
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
