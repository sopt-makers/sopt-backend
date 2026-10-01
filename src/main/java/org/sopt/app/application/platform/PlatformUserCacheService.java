package org.sopt.app.application.platform;

import java.time.Duration;
import java.util.function.Supplier;
import lombok.extern.slf4j.Slf4j;
import org.sopt.app.application.platform.dto.PlatformUserInfoResponse;
import org.sopt.app.common.cache.CachePolicy;
import org.sopt.app.common.cache.ResilientCacheTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.env.Environment;
import org.springframework.core.env.Profiles;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class PlatformUserCacheService {

    private static final String CACHE_KEY_PREFIX = "platformUserInfo::";
    private static final long LOGICAL_TTL_MS = 1000 * 60 * 5; // 5분
    private static final Duration DEFAULT_PHYSICAL_TTL = Duration.ofHours(6);
    private static final Duration ACTIVE_USER_PHYSICAL_TTL = Duration.ofDays(30);
    private final ResilientCacheTemplate resilientCacheTemplate;
    private final CachePolicy userCachePolicy;
    private final Long currentGeneration;

    // AWS Lambda 프로필에서는 동기 갱신으로 진행하기 위함
    public PlatformUserCacheService(
        ResilientCacheTemplate resilientCacheTemplate,
        Environment environment,
        @Value("${sopt.current.generation}") Long currentGeneration
    ) {
        this.resilientCacheTemplate = resilientCacheTemplate;
        this.currentGeneration = currentGeneration;

        // 람다 프로필 여부에 따라 비동기 갱신
        boolean isAsync = !environment.acceptsProfiles(Profiles.of("lambda"));

        this.userCachePolicy = CachePolicy.of(
            LOGICAL_TTL_MS,
            DEFAULT_PHYSICAL_TTL,
            isAsync
        );
    }

    public PlatformUserInfoResponse getPlatformUserInfo(Long userId, Supplier<PlatformUserInfoResponse> fetcher) {
        String cacheKey = CACHE_KEY_PREFIX + userId.toString();

        return resilientCacheTemplate.get(
            cacheKey,
            PlatformUserInfoResponse.class,
            userCachePolicy,
            fetcher,
            this::resolvePhysicalTtl
        );
    }

    /**
     * 물리 TTL 정책
     */
    private Duration resolvePhysicalTtl(PlatformUserInfoResponse user) {
        if (isCurrentGenerationActiveUser(user)) {
            return ACTIVE_USER_PHYSICAL_TTL;
        }
        return DEFAULT_PHYSICAL_TTL;
    }

    private boolean isCurrentGenerationActiveUser(PlatformUserInfoResponse user) {
        return currentGeneration != null && user.getLastSoptGeneration() == currentGeneration;
    }
}
