package org.sopt.app.facade;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.sopt.app.application.appservice.OperationConfigService;
import org.sopt.app.application.soptamp.SoptampUserService;
import org.sopt.app.application.stamp.ClapService;
import org.sopt.app.application.stamp.StampService;
import org.sopt.app.interfaces.postgres.ClapMilestoneGuard;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class AdminSoptampFacade {

    private final StampService stampService;
    private final SoptampUserService soptampUserService;
    private final ClapService clapService;
    private final ClapMilestoneGuard clapMilestoneGuard;
    private final OperationConfigService operationConfigService;

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

    @Transactional
    public void updateUpsertBatchSchedule(String cron) {
        operationConfigService.updateSoptampBatchConfig(cron);
        log.info("솝탬프 upsert 배치 스케줄 변경. cron={}", cron);
    }
}
