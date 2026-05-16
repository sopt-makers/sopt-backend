package org.sopt.app.common.cache;

import java.util.function.Supplier;

public interface ResilientCacheTemplate {

    /**
     * @param key 캐시 식별 키
     * @param type 반환받을 객체의 타입 클래스
     * @param policy 캐시 정책
     * @param fetcher 캐시 미스 시 데이터를 가져올 공급자(Supplier)
     */
    <T> T get(
        String key,
        Class<T> type,
        CachePolicy policy,
        Supplier<T> fetcher
    );
}
