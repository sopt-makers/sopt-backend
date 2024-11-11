package org.sopt.app.application.app_service;

import java.util.List;
import lombok.*;
import org.sopt.app.domain.entity.AppService;

@Getter
@ToString
@Builder
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class AppServiceInfo {

    private final AppServiceName serviceName;
    private final Boolean activeUser;
    private final Boolean inactiveUser;
    private final List<String> messages;
    private final List<String> messageColors;

    public static AppServiceInfo of(final AppService appService) {
        return AppServiceInfo.builder()
                .serviceName(appService.getServiceName())
                .activeUser(appService.getActiveUser())
                .inactiveUser(appService.getInactiveUser())
                .messages(appService.getMessages())
                .messageColors(appService.getMessageColors())
                .build();
    }
}
