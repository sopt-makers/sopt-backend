package org.sopt.app.application.appservice;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import java.time.Duration;
import java.util.Comparator;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.sopt.app.application.appservice.dto.AppServiceInfo;
import org.sopt.app.domain.entity.AppService;
import org.sopt.app.interfaces.postgres.AppServiceRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AppServiceService {

    private final AppServiceRepository appServiceRepository;

    private final Cache<String, List<AppServiceInfo>> appServiceCache = Caffeine.newBuilder()
        .expireAfterWrite(Duration.ofMinutes(10))
        .build();

    private static final String ALL_APP_SERVICE_CACHE_KEY = "all";

    @Transactional(readOnly = true)
    public List<AppServiceInfo> getAllAppService() {
        return appServiceCache.get(ALL_APP_SERVICE_CACHE_KEY, k -> loadAllAppService());
    }

    private List<AppServiceInfo> loadAllAppService() {
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
}
