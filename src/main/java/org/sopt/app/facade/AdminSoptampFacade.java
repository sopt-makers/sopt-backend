package org.sopt.app.facade;

import lombok.RequiredArgsConstructor;
import org.sopt.app.application.soptamp.*;
import org.sopt.app.application.stamp.StampService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AdminSoptampFacade {

    private final StampService stampService;
    private final SoptampUserService soptampUserService;

    @Transactional
    public void initAllMissionAndStampAndPoints() {
        stampService.deleteAll();
        soptampUserService.initAllSoptampUserPoints();
    }

    @Transactional
    public void initPoints() {
        soptampUserService.initAllSoptampUserPoints();
    }

    @Transactional
    public void initRankCache() {
        soptampUserService.initSoptampRankCache();
    }
}
