package org.sopt.app.application.stamp;

import lombok.RequiredArgsConstructor;
import lombok.val;
import org.sopt.app.common.exception.BadRequestException;
import org.sopt.app.common.response.ErrorCode;
import org.sopt.app.domain.entity.AppjamUser;
import org.sopt.app.domain.enums.TeamNumber;
import org.sopt.app.interfaces.postgres.AppjamUserRepository;
import org.sopt.app.interfaces.postgres.StampRepository;
import org.sopt.app.presentation.appjamtamp.AppjamtampRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AppjamStampService {

    private final AppjamUserRepository appjamUserRepository;
    private final StampRepository stampRepository;

    @Transactional(readOnly = true)
    public void checkDuplicateStamp(TeamNumber teamNumber, Long missionId) {
        val appjamUsers = appjamUserRepository.findAllByTeamNumber(teamNumber);
        val isDuplicated = stampRepository.existsByUserIdInAndMissionId(
            appjamUsers.stream().map(AppjamUser::getUserId).toList(), missionId);
        if (isDuplicated) {
            throw new BadRequestException(ErrorCode.DUPLICATE_STAMP);
        }
    }

    @Transactional
    public StampInfo.Stamp uploadStamp(
        AppjamtampRequest.RegisterStampRequest stampRequest,
        Long userId
    ) {
        val stamp = stampRequest.toStamp(userId);
        val newStamp = stampRepository.save(stamp);
        return StampInfo.Stamp.from(newStamp);
    }
}
