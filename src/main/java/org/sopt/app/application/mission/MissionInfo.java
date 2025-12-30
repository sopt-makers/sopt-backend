package org.sopt.app.application.mission;

import java.util.List;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.sopt.app.application.appjamuser.AppjamUserInfo.AppjamUserStatus;
import org.sopt.app.application.appjamuser.AppjamUserInfo.TeamSummary;
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
    public static class AppjamMissionInfo {

        private Long id;
        private String title;
        private String ownerName;
        private Integer level;
        private List<String> profileImage;
        private boolean isCompleted;

        public static AppjamMissionInfo of(
            Mission mission,
            boolean isCompleted,
            String ownerName
        ) {
            return AppjamMissionInfo.builder()
                .id(mission.getId())
                .title(mission.getTitle())
                .ownerName(ownerName)
                .level(mission.getLevel())
                .profileImage(mission.getProfileImage())
                .isCompleted(isCompleted)
                .build();
        }

        public static AppjamMissionInfo createWhenUncompleted(Mission mission) {
            return AppjamMissionInfo.builder()
                .id(mission.getId())
                .title(mission.getTitle())
                .level(mission.getLevel())
                .profileImage(mission.getProfileImage())
                .isCompleted(false)
                .build();
        }
    }

    @Getter
    @Builder
    @ToString
    public static class AppjamMissionInfos {

        private TeamNumber myTeamNumber;
        private boolean isAppjamJoined;
        private TeamNumber teamNumber;
        private String teamName;
        private List<AppjamMissionInfo> missions;

        public static AppjamMissionInfos of(
            AppjamUserStatus appjamUserStatus,
            TeamSummary teamSummary,
            List<AppjamMissionInfo> missions
        ) {
            return AppjamMissionInfos.builder()
                .myTeamNumber(appjamUserStatus.getTeamNumber())
                .isAppjamJoined(appjamUserStatus.isAppjamJoined())
                .teamNumber(teamSummary.getTeamNumber())
                .teamName(teamSummary.getTeamName())
                .missions(missions)
                .build();
        }
    }

}
