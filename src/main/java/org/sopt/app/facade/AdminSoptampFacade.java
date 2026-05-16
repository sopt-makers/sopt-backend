package org.sopt.app.facade;

import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.sopt.app.application.platform.PlatformService;
import org.sopt.app.application.platform.dto.PlatformUserInfoResponse;
import org.sopt.app.application.soptamp.SoptampUserService;
import org.sopt.app.application.stamp.ClapService;
import org.sopt.app.application.stamp.StampService;
import org.sopt.app.interfaces.postgres.ClapMilestoneGuard;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AdminSoptampFacade {

    private final StampService stampService;
    private final SoptampUserService soptampUserService;
    private final ClapService clapService;
    private final ClapMilestoneGuard clapMilestoneGuard;
    private final PlatformService platformService;

    @Transactional
    public void clearSoptampData(boolean stamp, boolean soptampUser) {
        if (stamp) {
            clapMilestoneGuard.deleteAll();
            clapService.deleteAll();
            stampService.deleteAllStampsWithImages();
        }
        if (soptampUser) {
            soptampUserService.deleteAllSoptampUsers();
        }
    }

    @Transactional
    public void initPoints() {
        soptampUserService.initAllSoptampUserPoints();
    }

    @Transactional
    public void initRankCache() {
        soptampUserService.initSoptampRankCache();
    }

    public void upsertAllSoptampUsers() {
        List<Long> targetUserIds = soptampUserService.getUpsertTargetUserIds();
        Map<Long, PlatformUserInfoResponse> profileMap = platformService.getPlatformUserInfosAsMap(targetUserIds);
        soptampUserService.upsertAllSoptampUsers(profileMap);
    }
}
