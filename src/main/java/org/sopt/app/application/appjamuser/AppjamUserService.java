package org.sopt.app.application.appjamuser;

import lombok.RequiredArgsConstructor;
import lombok.val;
import org.sopt.app.application.appjamuser.AppjamUserInfo.AppjamUserStatus;
import org.sopt.app.application.appjamuser.AppjamUserInfo.TeamSummary;
import org.sopt.app.common.exception.NotFoundException;
import org.sopt.app.common.response.ErrorCode;
import org.sopt.app.domain.enums.TeamNumber;
import org.sopt.app.interfaces.postgres.AppjamUserRepository;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AppjamUserService {

    private final AppjamUserRepository appjamUserRepository;

    public AppjamUserStatus getAppjamUserStatus(Long userId) {
        return appjamUserRepository.findByUserId(userId)
            .map(AppjamUserStatus::appjamJoined)
            .orElseGet(AppjamUserStatus::appjamNotJoined);
    }

    public TeamSummary getTeamSummaryByTeamNumber(TeamNumber teamNumber) {
        val appjamUser = appjamUserRepository.findTopByTeamNumberOrderById(teamNumber)
            .orElseThrow(() -> new NotFoundException(ErrorCode.TEAM_NOT_FOUND));
        return TeamSummary.from(appjamUser);
    }

    public TeamSummary getTeamSummaryByUserId(Long userId) {
        val appjamUser = appjamUserRepository.findByUserId(userId)
            .orElseThrow(() -> new NotFoundException(ErrorCode.TEAM_NOT_FOUND));
        return TeamSummary.from(appjamUser);
    }

    public boolean isAppjamParticipant(Long userId) {
        return appjamUserRepository.existsByUserId(userId);
    }
}
