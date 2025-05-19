package org.sopt.app.application.app_service;

import lombok.RequiredArgsConstructor;
import org.sopt.app.common.config.OperationConfig;
import org.sopt.app.common.config.OperationConfigCategory;
import org.sopt.app.common.exception.NotFoundException;
import org.sopt.app.common.response.ErrorCode;
import org.sopt.app.interfaces.postgres.OperationConfigRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class OperationConfigService {

    private final OperationConfigRepository operationConfigRepository;

    public List<OperationConfig> getOperationConfigByOperationConfigType(OperationConfigCategory operationConfigCategory) {
        return operationConfigRepository.findByOperationConfigCategory(operationConfigCategory).orElseThrow(
                () -> new NotFoundException(ErrorCode.ENTITY_NOT_FOUND));
    }
}
