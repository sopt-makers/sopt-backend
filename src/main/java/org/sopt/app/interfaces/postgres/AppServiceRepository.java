package org.sopt.app.interfaces.postgres;

import org.sopt.app.domain.entity.AppService;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AppServiceRepository extends JpaRepository<AppService, Long> {
    AppService findByServiceName(String serviceName);
}
