package org.sopt.app.facade;

import java.util.Objects;
import lombok.RequiredArgsConstructor;
import lombok.val;
import org.sopt.app.application.appjamuser.AppjamUserService;
import org.sopt.app.application.platform.PlatformService;
import org.sopt.app.application.soptamp.SoptampUserFinder;
import org.sopt.app.application.stamp.ClapService;
import org.sopt.app.application.stamp.StampInfo;
import org.sopt.app.application.stamp.StampInfo.AppjamtampView;
import org.sopt.app.application.stamp.StampService;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AppjamtampFacade {

    private final SoptampUserFinder soptampUserFinder;
    private final PlatformService platformService;
    private final StampService stampService;
    private final ClapService clapService;
    private final AppjamUserService appjamUserService;

    public AppjamtampView getAppjamtamps(Long requestUserId, Long missionId, String nickname) {
        val owner = soptampUserFinder.findByNickname(nickname);
        val ownerUserId = owner.getUserId();
        val platformUserInfoResponse = platformService.getPlatformUserInfoResponse(ownerUserId);
        val teamSummary = appjamUserService.getTeamSummaryByUserId(ownerUserId);
        val stamp = stampService.findStamp(missionId, ownerUserId);
        val requestUserClapCount = clapService.getUserClapCount(requestUserId, stamp.getId());
        stampService.increaseViewCountById(stamp.getId());

        return StampInfo.AppjamtampView.of(
            stamp,
            requestUserClapCount,
            Objects.equals(requestUserId, ownerUserId),
            owner.getNickname(),
            platformUserInfoResponse.profileImage(),
            teamSummary
        );
    }
}
