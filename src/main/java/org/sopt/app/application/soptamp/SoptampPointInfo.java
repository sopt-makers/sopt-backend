package org.sopt.app.application.soptamp;

import lombok.*;
import org.sopt.app.domain.enums.Part;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class SoptampPointInfo {

    @Getter
    @Builder
    @AllArgsConstructor(access = AccessLevel.PRIVATE)
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
    @AllArgsConstructor(access = AccessLevel.PRIVATE)
    public static class PartRank {
        private String part;
        private Integer rank;
        private Long points;
    }

    public record PartPoint(Part part, Long points) {

    }
}
