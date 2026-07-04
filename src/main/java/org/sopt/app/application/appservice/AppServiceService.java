package org.sopt.app.application.appservice;

import java.util.Comparator;
import java.util.List;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import org.sopt.app.application.appservice.dto.AppServiceInfo;
import org.sopt.app.domain.entity.AppService;
import org.sopt.app.interfaces.postgres.AppServiceRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AppServiceService {

    private static final Set<AppServiceName> HOME_APP_SERVICES = Set.of(
        AppServiceName.SOPT_LETTER
    );
    private static final Set<AppServiceName> TAB_APP_SERVICES = Set.of(
        AppServiceName.POKE,
        AppServiceName.SOPTAMP
    );

    private final AppServiceRepository appServiceRepository;

    @Transactional(readOnly = true)
    public List<AppServiceInfo> getHomeAppServices() {
        return getAppServices(HOME_APP_SERVICES);
    }

    @Transactional(readOnly = true)
    public List<AppServiceInfo> getTabAppServices() {
        return getAppServices(TAB_APP_SERVICES);
    }

    @Transactional(readOnly = true)
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

    @Transactional(readOnly = true)
    public AppServiceInfo getAppService(String serviceName) {
        return AppServiceInfo.of(appServiceRepository.findByServiceName(serviceName));
    }

    private List<AppServiceInfo> getAppServices(Set<AppServiceName> appServiceNames) {
        return getAllAppService().stream()
            .filter(appService -> appServiceNames.contains(AppServiceName.of(appService.getServiceName())))
            .toList();
    }
}
