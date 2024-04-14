package org.sopt.app.application.service;

import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.val;
import org.sopt.app.interfaces.postgres.AppServiceRepository;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AppServiceService {

    private final AppServiceRepository appServiceRepository;

    public List<AppServiceInfo.AppService> getAllAppService() {
        val appServiceList = appServiceRepository.findAll();
        return appServiceList.stream()
                .map(appService -> AppServiceInfo.AppService.builder()
                        .serviceName(appService.getServiceName())
                        .activeUser(appService.getActiveUser())
                        .inactiveUser(appService.getInactiveUser())
                        .build())
                .collect(Collectors.toList());
    }
}
