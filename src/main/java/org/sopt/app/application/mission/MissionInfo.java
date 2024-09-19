package org.sopt.app.application.mission;

import java.util.List;
import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

public class MissionInfo {

    @Getter
    @Builder
    @ToString
    public static class Completeness {

        private Long id;
        private String title;
        private Integer level;
        private List<String> profileImage;
        private Boolean isCompleted;
    }

    @Getter
    @Builder
    @ToString
    public static class Level {
        private Integer level;

        public static Level of(Integer level) {
            return new Level(level);
        }
    }
}
