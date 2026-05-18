package org.sopt.app.application.appservice;

import lombok.RequiredArgsConstructor;
import org.sopt.app.common.config.OperationConfig;
import org.sopt.app.common.config.OperationConfigCategory;
import org.sopt.app.common.exception.NotFoundException;
import org.sopt.app.common.response.ErrorCode;
import org.sopt.app.interfaces.postgres.OperationConfigRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class OperationConfigService {

    private static final String SOPTAMP_UPSERT_CRON_KEY = "UPSERT_CRON";
    private static final String DEFAULT_UPSERT_CRON = "0 0 3 * * *"; // 기본값: 매일 새벽 3시

    private final OperationConfigRepository operationConfigRepository;

    @Transactional(readOnly = true)
    public List<OperationConfig> getOperationConfigByOperationConfigType(OperationConfigCategory operationConfigCategory) {
        return operationConfigRepository.findByOperationConfigCategory(operationConfigCategory).orElseThrow(
                () -> new NotFoundException(ErrorCode.ENTITY_NOT_FOUND));
    }

    @Transactional(readOnly = true)
    public String getSoptampUpsertCron() {
        return operationConfigRepository
            .findByOperationConfigCategoryAndKey(OperationConfigCategory.SOPTAMP_BATCH, SOPTAMP_UPSERT_CRON_KEY)
            .map(OperationConfig::getValue)
            .orElse(DEFAULT_UPSERT_CRON);
    }

    @Transactional
    public void updateSoptampBatchConfig(String cron) {
        upsertConfig(SOPTAMP_UPSERT_CRON_KEY, cron, "솝탬프 upsert 배치 실행 cron 표현식");
    }

    private void upsertConfig(String key, String value, String description) {
        operationConfigRepository
            .findByOperationConfigCategoryAndKey(OperationConfigCategory.SOPTAMP_BATCH, key)
            .ifPresentOrElse(
                config -> config.updateValue(value),
                () -> operationConfigRepository.save(
                    OperationConfig.of(OperationConfigCategory.SOPTAMP_BATCH, key, value, description))
            );
    }
}
