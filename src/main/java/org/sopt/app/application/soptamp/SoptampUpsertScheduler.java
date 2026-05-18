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

    /**
     * DB에 저장된 cron 표현식을 매 실행 직전에 읽어 동적으로 스케줄을 적용한다.
     * PATCH /api/v2/admin/soptamp/upsert/schedule 으로 cron 변경 시 다음 실행부터 반영.
     *
     * DB 미설정 시 기본값: 매일 새벽 3시 (0 0 3 * * *)
     */
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
