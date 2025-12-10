package org.sopt.app.application.mission;

import java.util.List;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.sopt.app.domain.entity.TeamInfo;
import org.sopt.app.domain.entity.soptamp.Mission;
import org.sopt.app.domain.enums.TeamNumber;

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
            String ownerName
        ) {
            return TeamMissionInfo.builder()
                .id(mission.getId())
                .title(mission.getTitle())
                .ownerName(ownerName)
                .level(mission.getLevel())
                .profileImage(mission.getProfileImage())
                .isCompleted(isCompleted)
                .build();
        }
    }

    @Getter
    @Builder
    @ToString
    public static class TeamMissionInfos {

        private TeamNumber teamNumber;
        private String teamName;
        private List<TeamMissionInfo> missions;

        public static TeamMissionInfos of(
            TeamSummary teamSummary,
            List<TeamMissionInfo> missions
        ) {
            return TeamMissionInfos.builder()
                .teamNumber(teamSummary.teamNumber)
                .teamName(teamSummary.teamName)
                .missions(missions)
                .build();
        }
    }

    @Getter
    @Builder
    @ToString
    public static class TeamSummary {

        private TeamNumber teamNumber;
        private String teamName;

        public static TeamSummary from(TeamInfo teamInfo) {
            return TeamSummary.builder().
                teamNumber(teamInfo.getTeamNumber())
                .teamName(teamInfo.getTeamName())
                .build();
        }
    }
}
