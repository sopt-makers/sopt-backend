package org.sopt.app.common.cache;

import java.time.Duration;

/**
 *
 * @param logicalTtlMs 논리 만료 시간 (밀리세컨드) - 비동기 갱신을 위함
 * @param physicalTtl 물리적 만료 시간 - Redis TTL
 * @param asyncRefreshEnabled 비동기 갱신 여부 - 람다와 인스턴스 환경 분리를 위함
 * @param lockTtl Redis 락 유지 시간
 * @param maxWaitTime 락 || 걍신 대기 시간
 */
public record CachePolicy(
    long logicalTtlMs,
    Duration physicalTtl,
    boolean asyncRefreshEnabled,
    Duration lockTtl,
    Duration maxWaitTime
) {

    public static CachePolicy of(
        long logicalTtlMs,
        Duration physicalTtl,
        boolean async
    ) {
        return new CachePolicy(
            logicalTtlMs,
            physicalTtl,
            async,
            Duration.ofSeconds(5),
            Duration.ofSeconds(3)
        );
    }
}
