package org.sopt.app.facade;

import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.val;
import org.sopt.app.application.appjam_user.AppjamUserService;
import org.sopt.app.application.mission.AppjamMissionService;
import org.sopt.app.application.mission.MissionInfo.AppjamMissionInfos;
import org.sopt.app.domain.enums.TeamNumber;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class MissionFacade {

    private final AppjamMissionService appjamMissionService;
    private final AppjamUserService appjamUserService;

    @Transactional(readOnly = true)
    public AppjamMissionInfos getTeamMissions(
        TeamNumber teamNumber,
        Optional<Boolean> complete
    ) {
        val teamSummary = appjamUserService.getTeamSummaryByTeamNumber(teamNumber);
        if (complete.isPresent()) {
            return AppjamMissionInfos.of(
                teamSummary,
                appjamMissionService.getMissionsByCondition(teamNumber, complete.get()));
        }
        return AppjamMissionInfos.of(teamSummary, appjamMissionService.getAllMissions(teamNumber));
    }

}
