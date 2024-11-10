package org.sopt.app.application.app_service;

import lombok.*;

@Getter
@ToString
public class AppServiceInfo {

    private final AppServiceName serviceName;
    private final Boolean activeUser;
    private final Boolean inactiveUser;

    @Builder
    private AppServiceInfo(AppServiceName serviceName, Boolean activeUser, Boolean inactiveUser) {
        this.serviceName = serviceName;
        this.activeUser = activeUser;
        this.inactiveUser = inactiveUser;
    }
}
