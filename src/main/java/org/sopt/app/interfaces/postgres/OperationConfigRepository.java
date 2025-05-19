package org.sopt.app.interfaces.postgres;

import org.sopt.app.common.config.OperationConfig;
import org.sopt.app.common.config.OperationConfigCategory;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface OperationConfigRepository extends JpaRepository<OperationConfig, Long> {
    Optional<List<OperationConfig>> findByOperationConfigCategory(OperationConfigCategory operationConfigCategory);
}
