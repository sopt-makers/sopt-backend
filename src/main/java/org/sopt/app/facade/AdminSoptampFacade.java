package org.sopt.app.facade;

import lombok.RequiredArgsConstructor;
import org.sopt.app.application.mission.MissionService;
import org.sopt.app.application.soptamp.SoptampPointService;
import org.sopt.app.application.soptamp.SoptampUserService;
import org.sopt.app.application.stamp.StampService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AdminSoptampFacade {

    private final MissionService missionService;
    private final StampService stampService;
    private final SoptampPointService soptampPointService;
    private final SoptampUserService soptampUserService;

    @Transactional
    public void deleteAllMissionAndStamp() {
        validateAdminUser();
        missionService.deleteAll();
        stampService.deleteAll();
        soptampPointService.deleteAll();
        soptampUserService.initAllSoptampUserPoints();
    }

    private void validateAdminUser() {
        // TODO: Admin User
    }
}
