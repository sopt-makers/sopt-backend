package org.sopt.app.application.mission;

import java.util.List;
import java.util.Optional;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.sopt.app.domain.entity.soptamp.Mission;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
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

    @Getter
    @Builder
    @ToString
    public static class TeamMissionInfo {

        private Long id;
        private String title;
        private String ownerName;
        private Integer level;
        private List<String> profileImage;
        private boolean isCompleted;

        public static TeamMissionInfo of(
            Mission mission,
            boolean isCompleted,
            Optional<String> ownerName
        ) {
            return TeamMissionInfo.builder()
                .id(mission.getId())
                .title(mission.getTitle())
                .ownerName(ownerName.orElse(null))
                .level(mission.getLevel())
                .profileImage(mission.getProfileImage())
                .isCompleted(isCompleted)
                .build();
        }
    }
}
