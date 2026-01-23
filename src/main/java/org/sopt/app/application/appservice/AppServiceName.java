package org.sopt.app.application.appservice;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum AppServiceName {
    POKE("콕찌르기", "POKE","pokeBadgeManager"),
    SOPTAMP("솝탬프", "SOPTAMP","soptampBadgeManager"),
    FORTUNE("솝마디", "FORTUNE","fortuneBadgeManager"),
    OTHERS("", "OTHERS","defaultBadgeManager"),
    FLOATING_BUTTON("FAB", "FLOATING_BUTTON", "floatingButtonBadgeManager"),
    REVIEW_FORM("후기폼", "REVIEW_FORM", "reviewFormBadgeManager");

    private final String exposedName;
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
