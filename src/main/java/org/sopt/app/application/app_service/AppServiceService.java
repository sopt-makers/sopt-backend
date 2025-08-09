package org.sopt.app.application.app_service;

import java.util.Comparator;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.sopt.app.application.app_service.dto.AppServiceInfo;
import org.sopt.app.domain.entity.AppService;
import org.sopt.app.interfaces.postgres.AppServiceRepository;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AppServiceService {

    private final AppServiceRepository appServiceRepository;

    public List<AppServiceInfo> getAllAppService() {
        return appServiceRepository.findAll().stream()
                .filter(appService -> !(
                        AppServiceName.of(appService.getServiceName()).equals(AppServiceName.OTHERS) ||
                        AppServiceName.of(appService.getServiceName()).equals(AppServiceName.FLOATING_BUTTON) ||
                            AppServiceName.of(appService.getServiceName()).equals(AppServiceName.REVIEW_FORM)
                ))
                .sorted(Comparator.comparing(AppService::getCreatedAt).reversed())
                .map(AppServiceInfo::of)
                .toList();
    }

    public AppServiceInfo getAppService(String serviceName) {
        return AppServiceInfo.of(appServiceRepository.findByServiceName(serviceName));
    }
}
