package org.sopt.app.application.app_service.dto;

import lombok.*;
import org.sopt.app.domain.entity.AppService;

@Getter
@ToString
@Builder
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class AppServiceInfo {

    private final String serviceName;
    private final Boolean activeUser;
    private final Boolean inactiveUser;

    public static AppServiceInfo of(final AppService appService) {
        return AppServiceInfo.builder()
                .serviceName(appService.getServiceName())
                .activeUser(appService.getActiveUser())
                .inactiveUser(appService.getInactiveUser())
                .build();
    }
}
