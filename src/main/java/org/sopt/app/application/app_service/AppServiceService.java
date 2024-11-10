package org.sopt.app.application.app_service;

import java.util.Comparator;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.sopt.app.domain.entity.AppService;
import org.sopt.app.interfaces.postgres.AppServiceRepository;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AppServiceService {

    private final AppServiceRepository appServiceRepository;

    public List<AppServiceInfo.AppService> getAllAppService() {
        return appServiceRepository.findAll().stream()
                .sorted(Comparator.comparing(AppService::getCreatedAt).reversed())
                .map(appService -> AppServiceInfo.AppService.builder()
                        .serviceName(appService.getServiceName())
                        .activeUser(appService.getActiveUser())
                        .inactiveUser(appService.getInactiveUser())
                        .build())
                .toList();
    }
}
