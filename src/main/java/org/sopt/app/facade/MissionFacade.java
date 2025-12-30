package org.sopt.app.facade;

import lombok.RequiredArgsConstructor;
import lombok.val;
import org.sopt.app.application.appjamuser.AppjamUserInfo.AppjamUserStatus;
import org.sopt.app.application.appjamuser.AppjamUserInfo.TeamSummary;
import org.sopt.app.application.appjamuser.AppjamUserService;
import org.sopt.app.application.mission.AppjamMissionService;
import org.sopt.app.application.mission.MissionInfo.AppjamMissionInfos;
import org.sopt.app.application.mission.MissionService;
import org.sopt.app.domain.enums.TeamNumber;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class MissionFacade {

    private final AppjamMissionService appjamMissionService;
    private final AppjamUserService appjamUserService;
    private final MissionService missionService;

    @Transactional(readOnly = true)
    public AppjamMissionInfos getTeamMissions(
        Long userId,
        @Nullable TeamNumber teamNumber,
        @Nullable Boolean complete
    ) {
        if (teamNumber == null) {
            val missions = appjamMissionService.getDisplayedMissions();
            return AppjamMissionInfos.of(AppjamUserStatus.appjamNotJoined(), TeamSummary.empty(),
                missions);
        }

        val teamSummary = appjamUserService.getTeamSummaryByTeamNumber(teamNumber);
        val appjamUserStatus = appjamUserService.getAppjamUserStatus(userId);
        if (complete != null) {
            return AppjamMissionInfos.of(
                appjamUserStatus,
                teamSummary,
                appjamMissionService.getMissionsByTeamAndCondition(teamNumber, complete));
        }
        return AppjamMissionInfos.of(appjamUserStatus, teamSummary,
            appjamMissionService.getMissionsByTeam(teamNumber));
    }

}
