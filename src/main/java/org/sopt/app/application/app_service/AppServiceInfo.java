package org.sopt.app.application.app_service;

import lombok.*;

@Getter
@ToString
public class AppServiceInfo {

    private AppServiceName serviceName;
    private Boolean activeUser;
    private Boolean inactiveUser;

    @Builder
    private AppServiceInfo(AppServiceName serviceName, Boolean activeUser, Boolean inactiveUser) {
        this.serviceName = serviceName;
        this.activeUser = activeUser;
        this.inactiveUser = inactiveUser;
    }
}
