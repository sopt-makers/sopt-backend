package org.sopt.app.facade;

import java.util.Objects;
import lombok.RequiredArgsConstructor;
import lombok.val;
import org.sopt.app.application.appjamuser.AppjamUserInfo.AppjamUserStatus;
import org.sopt.app.application.appjamuser.AppjamUserService;
import org.sopt.app.application.mission.MissionInfo.Level;
import org.sopt.app.application.mission.MissionService;
import org.sopt.app.application.platform.PlatformService;
import org.sopt.app.application.soptamp.SoptampUserFinder;
import org.sopt.app.application.soptamp.SoptampUserService;
import org.sopt.app.application.stamp.AppjamStampService;
import org.sopt.app.application.stamp.ClapService;
import org.sopt.app.application.stamp.StampInfo;
import org.sopt.app.application.stamp.StampInfo.AppjamtampView;
import org.sopt.app.application.stamp.StampService;
import org.sopt.app.common.exception.BadRequestException;
import org.sopt.app.common.response.ErrorCode;
import org.sopt.app.presentation.appjamtamp.AppjamtampRequest.RegisterStampRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AppjamtampFacade {

    private final SoptampUserFinder soptampUserFinder;
    private final PlatformService platformService;
    private final StampService stampService;
    private final ClapService clapService;
    private final AppjamUserService appjamUserService;
    private final AppjamStampService appjamStampService;
    private final SoptampUserService soptampUserService;
    private final MissionService missionService;

    @Transactional(readOnly = true)
    public AppjamtampView getAppjamtamps(Long requestUserId, Long missionId, String nickname) {
        val owner = soptampUserFinder.findByNickname(nickname);
        val ownerUserId = owner.getUserId();
        val platformUserInfoResponse = platformService.getPlatformUserInfoResponse(ownerUserId);
        val teamSummary = appjamUserService.getTeamSummaryByUserId(ownerUserId);
        val stamp = stampService.findStamp(missionId, ownerUserId);
        val requestUserClapCount = clapService.getUserClapCount(requestUserId, stamp.getId());
        val mission = missionService.getMissionById(missionId);
        stampService.increaseViewCountById(stamp.getId());

        return StampInfo.AppjamtampView.of(
            stamp,
            requestUserClapCount,
            Objects.equals(requestUserId, ownerUserId),
            owner.getNickname(),
            platformUserInfoResponse.profileImage(),
            teamSummary,
            mission
        );
    }

    @Transactional
    public StampInfo.StampWithProfile uploadStamp(Long userId, RegisterStampRequest registerStampRequest) {
        val appjamUserStatus = appjamUserService.getAppjamUserStatus(userId);
        if (!appjamUserStatus.isAppjamJoined()) {
            throw new BadRequestException(ErrorCode.TEAM_FORBIDDEN);
        }
        appjamStampService.checkDuplicateStamp(appjamUserStatus.getTeamNumber(),
            registerStampRequest.getMissionId());
        val result = appjamStampService.uploadStamp(registerStampRequest, userId);
        Level mission = missionService.getMissionLevelById(registerStampRequest.getMissionId());
        soptampUserService.addPointByLevel(userId, mission.getLevel());
        val soptampUser = soptampUserFinder.findById(userId);
        val userInfo = platformService.getPlatformUserInfoResponse(userId);
        return StampInfo.StampWithProfile.of(result, soptampUser.getNickname(), userInfo.profileImage());
    }

    public AppjamUserStatus getAppjampStatus(Long userId){
        return appjamUserService.getAppjamUserStatus(userId);
    }
}
