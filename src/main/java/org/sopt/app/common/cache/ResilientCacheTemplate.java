package org.sopt.app.common.cache;

import java.time.Duration;
import java.util.function.Supplier;

public interface ResilientCacheTemplate {

    /**
     * @param key 캐시 식별 키
     * @param type 반환받을 객체의 타입 클래스
     * @param logicalTtlMs 논리 만료 시간 (밀리세컨드) - 비동기 갱신을 위함
     * @param physicalTtl 물리적 만료 시간 - Redis TTL
     * @param asyncRefreshEnabled 비동기 갱신 여부 - 람다와 인스턴스 환경 분리를 위함
     * @param fetcher 캐시 미스 시 데이터를 가져올 공급자(Supplier)
     */
    <T> T get(
        String key,
        Class<T> type,
        long logicalTtlMs,
        Duration physicalTtl,
        boolean asyncRefreshEnabled,
        Supplier<T> fetcher
    );
}
