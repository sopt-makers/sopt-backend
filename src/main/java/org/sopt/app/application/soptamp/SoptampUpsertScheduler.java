package org.sopt.app.application.soptamp;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.sopt.app.application.appservice.OperationConfigService;
import org.springframework.context.annotation.Profile;
import org.springframework.scheduling.annotation.SchedulingConfigurer;
import org.springframework.scheduling.config.ScheduledTaskRegistrar;
import org.springframework.scheduling.support.CronTrigger;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@Profile("!lambda")
@RequiredArgsConstructor
public class SoptampUpsertScheduler implements SchedulingConfigurer {

    private final SoptampBatchService soptampBatchService;
    private final OperationConfigService operationConfigService;

    @Override
    public void configureTasks(ScheduledTaskRegistrar registrar) {
        registrar.addTriggerTask(
            this::runUpsertBatch,
            context -> {
                String cron = operationConfigService.getSoptampUpsertCron();
                return new CronTrigger(cron).nextExecution(context);
            }
        );
    }

    void runUpsertBatch() {
        log.info("솝탬프 upsert 배치 자동 실행 시작");
        soptampBatchService.upsertAllSoptampUsers();
    }
}
