package org.sopt.app.application.app_service;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class AppServiceInfo {

    @Getter
    @Builder
    @ToString
    public static class AppService {

        private AppServiceName serviceName;
        private Boolean activeUser;
        private Boolean inactiveUser;

    }
}
