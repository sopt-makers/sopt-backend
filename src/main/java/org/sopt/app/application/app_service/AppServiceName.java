package org.sopt.app.application.app_service;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum AppServiceName {
    POKE("pokeBadgeManager"),
    SOPTAMP("soptampBadgeManager"),
    FORTUNE("fortuneBadgeManager");

    private final String badgeManagerName;
}
