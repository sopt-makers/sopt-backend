package org.sopt.app.application.platform;

import java.time.Duration;
import java.util.function.Supplier;
import lombok.extern.slf4j.Slf4j;
import org.sopt.app.application.platform.dto.PlatformUserInfoResponse;
import org.sopt.app.common.cache.ResilientCacheTemplate;
import org.springframework.core.env.Environment;
import org.springframework.core.env.Profiles;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class PlatformUserCacheService {

    private static final String CACHE_KEY_PREFIX = "platformUserInfo::";
    private static final long LOGICAL_TTL_MS = 1000 * 60 * 5; // 5분
    private static final Duration PHYSICAL_TTL = Duration.ofHours(6);

    private final ResilientCacheTemplate resilientCacheTemplate;
    private final boolean asyncRefreshEnabled;

    // AWS Lambda 프로필에서는 동기 갱신으로 진행하기 위함
    public PlatformUserCacheService(ResilientCacheTemplate resilientCacheTemplate, Environment environment) {
        this.resilientCacheTemplate = resilientCacheTemplate;
        this.asyncRefreshEnabled = !environment.acceptsProfiles(Profiles.of("lambda"));
    }

    public PlatformUserInfoResponse getPlatformUserInfo(Long userId, Supplier<PlatformUserInfoResponse> fetcher) {
        String cacheKey = CACHE_KEY_PREFIX + userId.toString();

        return resilientCacheTemplate.get(
            cacheKey,
            PlatformUserInfoResponse.class,
            LOGICAL_TTL_MS,
            PHYSICAL_TTL,
            asyncRefreshEnabled,
            fetcher
        );
    }
}