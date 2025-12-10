package org.sopt.app.facade;

import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.sopt.app.application.mission.MissionInfo.TeamMissionInfos;
import org.sopt.app.application.mission.MissionInfo.TeamSummary;
import org.sopt.app.application.mission.TeamMissionService;
import org.sopt.app.domain.enums.TeamNumber;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MissionFacade {

    private final TeamMissionService teamMissionService;

    public TeamMissionInfos getMissions(
        TeamNumber teamNumber,
        Optional<Boolean> complete
    ) {
        TeamSummary teamSummary = teamMissionService.getTeamSummary(teamNumber);
        if (complete.isPresent()) {
            return TeamMissionInfos.of(
                teamSummary, teamMissionService.getMissionsByCondition(teamNumber, complete.get()));
        }
        return TeamMissionInfos.of(teamSummary, teamMissionService.getAllMissions(teamNumber));
    }

}
