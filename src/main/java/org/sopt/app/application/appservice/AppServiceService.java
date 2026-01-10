package org.sopt.app.application.appservice;

import java.util.Comparator;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.sopt.app.application.appservice.dto.AppServiceInfo;
import org.sopt.app.domain.entity.AppService;
import org.sopt.app.interfaces.postgres.AppServiceRepository;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AppServiceService {

    private final AppServiceRepository appServiceRepository;

    public List<AppServiceInfo> getAllAppService() {
        return appServiceRepository.findAll().stream()
            .filter(appService -> {
                AppServiceName appServiceName =
                    AppServiceName.of(appService.getServiceName());
                return appServiceName != AppServiceName.OTHERS
                    && appServiceName != AppServiceName.FLOATING_BUTTON
                    && appServiceName != AppServiceName.REVIEW_FORM
                    && appServiceName != AppServiceName.FORTUNE;
            })
            .sorted(Comparator.comparing(AppService::getCreatedAt).reversed())
            .map(AppServiceInfo::of)
            .toList();
    }

    public AppServiceInfo getAppService(String serviceName) {
        return AppServiceInfo.of(appServiceRepository.findByServiceName(serviceName));
    }
}
