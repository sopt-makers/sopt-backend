package org.sopt.app.application.app_service;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum AppServiceName {
    POKE("POKE","pokeBadgeManager"),
    SOPTAMP("SOPTAMP","soptampBadgeManager"),
    FORTUNE("FORTUNE","fortuneBadgeManager"),
    OTHERS("OTHERS","defaultBadgeManager");

    private final String serviceName;
    private final String badgeManagerName;

    public static AppServiceName of(final String serviceName) {
        for (AppServiceName appServiceName : values()) {
            if (appServiceName.serviceName.equals(serviceName)) {
                return appServiceName;
            }
        }
        return OTHERS;
    }
}
