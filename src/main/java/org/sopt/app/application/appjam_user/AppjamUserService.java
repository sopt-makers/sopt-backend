package org.sopt.app.application.appjam_user;

import lombok.RequiredArgsConstructor;
import org.sopt.app.application.appjam_user.AppjamUserInfo.TeamSummary;
import org.sopt.app.common.exception.NotFoundException;
import org.sopt.app.common.response.ErrorCode;
import org.sopt.app.domain.entity.AppjamUser;
import org.sopt.app.domain.enums.TeamNumber;
import org.sopt.app.interfaces.postgres.AppjamUserRepository;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AppjamUserService {

    private final AppjamUserRepository appjamUserRepository;

    public TeamSummary getTeamSummaryByTeamNumber(TeamNumber teamNumber) {
        AppjamUser appjamUser = appjamUserRepository.findTopByTeamNumberOrderById(teamNumber)
            .orElseThrow(() -> new NotFoundException(ErrorCode.TEAM_NOT_FOUND));
        return TeamSummary.from(appjamUser);
    }

    public TeamSummary getTeamSummaryByUserId(Long userId) {
        AppjamUser appjamUser = appjamUserRepository.findByUserId(userId)
            .orElseThrow(() -> new NotFoundException(ErrorCode.TEAM_NOT_FOUND));
        return TeamSummary.from(appjamUser);
    }
}
