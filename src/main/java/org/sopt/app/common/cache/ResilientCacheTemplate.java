package org.sopt.app.common.cache;

import java.time.Duration;
import java.util.function.Function;
import java.util.function.Supplier;

public interface ResilientCacheTemplate {

    /**
     * @param key 캐시 식별 키
     * @param type 반환받을 객체의 타입 클래스
     * @param policy 캐시 정책
     * @param fetcher 캐시 미스 시 데이터를 가져올 공급자(Supplier)
     */
    default <T> T get(
        String key,
        Class<T> type,
        CachePolicy policy,
        Supplier<T> fetcher
    ) {
        // 물리 TTL을 별도로 계산하지 않는 기본 경로. 정책 고정값을 그대로 사용.
        return get(key, type, policy, fetcher, data -> policy.physicalTtl());
    }

    /**
     * @param key 캐시 식별 키
     * @param type 반환받을 객체의 타입 클래스
     * @param policy 캐시 정책
     * @param fetcher 캐시 미스 시 데이터를 가져올 공급자(Supplier)
     * @param physicalTtlResolver fetch된 데이터로 물리 TTL(Redis TTL)을 계산하는 함수
     */
    <T> T get(
        String key,
        Class<T> type,
        CachePolicy policy,
        Supplier<T> fetcher,
        Function<T, Duration> physicalTtlResolver
    );
}
