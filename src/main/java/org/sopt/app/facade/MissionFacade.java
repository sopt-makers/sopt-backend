package org.sopt.app.facade;

import lombok.RequiredArgsConstructor;
import lombok.val;
import org.sopt.app.application.appjamuser.AppjamUserInfo.TeamSummary;
import org.sopt.app.application.appjamuser.AppjamUserService;
import org.sopt.app.application.mission.AppjamMissionService;
import org.sopt.app.application.mission.MissionInfo.AppjamMissionInfos;
import org.sopt.app.domain.enums.TeamNumber;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class MissionFacade {

    private final AppjamMissionService appjamMissionService;
    private final AppjamUserService appjamUserService;

    @Transactional(readOnly = true)
    public AppjamMissionInfos getTeamMissions(
        Long userId,
        @Nullable TeamNumber teamNumber,
        @Nullable Boolean complete
    ) {
        val teamSummary = resolveTeamSummary(userId, teamNumber);
        val appjamUserStatus = appjamUserService.getAppjamUserStatus(userId);

        return AppjamMissionInfos.of(appjamUserStatus, teamSummary,
            appjamMissionService.getMissions(teamSummary.getTeamNumber(), complete));
    }

    private TeamSummary resolveTeamSummary(Long userId, TeamNumber teamNumber) {
        if (teamNumber != null) {
            return appjamUserService.getTeamSummaryByTeamNumber(teamNumber);
        }
        return appjamUserService.getTeamSummaryByUserId(userId);
    }

}
