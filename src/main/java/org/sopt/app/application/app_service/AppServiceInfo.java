package org.sopt.app.application.app_service;

import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

public class AppServiceInfo {

    @Getter
    @Builder
    @ToString
    public static class AppService {

        private String serviceName;
        private Boolean activeUser;
        private Boolean inactiveUser;

    }
}
