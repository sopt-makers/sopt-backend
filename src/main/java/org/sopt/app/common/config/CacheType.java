package org.sopt.app.common.config;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum CacheType {
    SOPTAMP_SCORE("soptamp_score", 12, 500),
    SOPTAMP_PROFILE_MESSAGE("soptamp_profile_message", 12, 500)
    ;

    private final String cacheName;
    private final int expiredAfterWrite;
    private final int maximumSize;
}