package org.sopt.app.common.cache;

import static org.sopt.app.common.config.AsyncConfig.CACHE_REFRESH_EXECUTOR;

import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.time.Duration;
import java.util.Collections;
import java.util.UUID;
import java.util.concurrent.Executor;
import java.util.concurrent.ThreadLocalRandom;
import java.util.function.Supplier;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.sopt.app.common.exception.BaseException;
import org.sopt.app.common.response.ErrorCode;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.data.redis.core.script.RedisScript;
import org.springframework.stereotype.Component;


@Slf4j
@Component
@RequiredArgsConstructor
public class RedisResilientCacheTemplate implements ResilientCacheTemplate {

    private final StringRedisTemplate stringRedisTemplate;
    private final ObjectMapper objectMapper;

    @Qualifier(CACHE_REFRESH_EXECUTOR)
    private final ObjectProvider<Executor> executorProvider;

    private static final String LOCK_PREFIX = "lock:cache:";
    private static final String REFRESH_MARKER_PREFIX = "refresh_marker:cache:";

    private static final long BASE_SLEEP_MS = 50;
    private static final long MAX_JITTER_MS = 50;

    private static final RedisScript<Long> RELEASE_LOCK_SCRIPT = new DefaultRedisScript<>(
        "if redis.call('get', KEYS[1]) == ARGV[1] then return redis.call('del', KEYS[1]) else return 0 end",
        Long.class
    );


    // Redis에 저장할 데이터 형태. data + 저장 시간을 묶어서 처리
    public record CacheWrapper<T>(T data, Long requestedAt) {}

    /**
     * 데이터가 존재할 경우, 논리 TTL에 따라 반환 only, 혹은 반환 후 비동기 갱신 작업을 수행.
     * 논리 TTL이 만료됐더라도, 우선 stale한 데이터를 반환함.
     */
    @Override
    public <T> T get(String key, Class<T> type, CachePolicy policy, Supplier<T> fetcher) {

        JavaType wrapperType = objectMapper.getTypeFactory().constructParametricType(CacheWrapper.class, type);

        CacheWrapper<T> cachedData = readCache(key, wrapperType);
        CacheWrapper<T> staleData = null;

        if(isValid(cachedData)){
            if(isFresh(cachedData, policy.logicalTtlMs())){
                return cachedData.data();
            }

            staleData = cachedData;

            if(policy.asyncRefreshEnabled()){
                dispatchAsyncRefresh(key, policy, fetcher, wrapperType);
                return cachedData.data();
            }
        }

        return blockingGet(key, policy, fetcher, staleData, wrapperType);
    }

    private <T> CacheWrapper<T> readCache(String key, JavaType wrapperType) {
        String cachedData = stringRedisTemplate.opsForValue().get(key);
        if (cachedData == null) return null;

        try {
            return objectMapper.readValue(cachedData, wrapperType);
        } catch (Exception e) {
            log.error("캐시 역직렬화 실패 (키: {})", key, e);
            return null;
        }
    }

    /**
     * stale 데이터도 없는 경우 or AWS Lambda 환경인 경우.
     */
    private <T> T blockingGet(String key, CachePolicy policy, Supplier<T> fetcher, CacheWrapper<T> staleData, JavaType wrapperType) {
        String lockKey = LOCK_PREFIX + key;
        String lockValue = UUID.randomUUID().toString();
        long startTime = System.currentTimeMillis();

        // 총 경과 시간이 MAX_WAIT_MS 를 넘지 않는 동안 반복
        while (System.currentTimeMillis() - startTime < policy.maxWaitTime().toMillis()) {

            CacheWrapper<T> wrapper = readCache(key, wrapperType);
            if (isValid(wrapper)) {
                if (isFresh(wrapper, policy.logicalTtlMs())) return wrapper.data();
                if (staleData == null) staleData = wrapper;
            }

            if (tryAcquireLock(lockKey, lockValue, policy.lockTtl())) {
                try {
                    CacheWrapper<T> doubleChecked = readCache(key, wrapperType);
                    if (isValid(doubleChecked) && isFresh(doubleChecked, policy.logicalTtlMs())) {
                        return doubleChecked.data();
                    }
                    return fetchAndCache(key, policy.physicalTtl(), fetcher, wrapperType);
                } finally {
                    releaseLock(lockKey, lockValue);
                }
            }

            sleepWithJitter();
        }

        if (staleData != null) {
            log.warn("캐시 갱신 락 획득 대기 시간이 초과되어 Stale 데이터를 반환. (Key: {})", key);
            return staleData.data();
        }
        throw new BaseException("캐시 갱신을 위한 락 획득 시간 초과", ErrorCode.INTERNAL_SERVER_ERROR);
    }

    /**
     * 스레드 sleep 시간에 jitter를 두어 부하 분산
     */
    private void sleepWithJitter() {
        try {
            long jitter = ThreadLocalRandom.current().nextLong(MAX_JITTER_MS);
            long sleepTime = BASE_SLEEP_MS + jitter;

            Thread.sleep(sleepTime);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new BaseException("락 대기 중 인터럽트 발생.", ErrorCode.INTERNAL_SERVER_ERROR);
        }
    }

    private boolean tryAcquireLock(String lockKey, String lockValue, Duration lockTtl) {
        return Boolean.TRUE.equals(stringRedisTemplate.opsForValue().setIfAbsent(lockKey, lockValue, lockTtl));
    }

    /**
     * 비동기적으로 이미 누군가 락을 잡고 작업 중이라면 패스, 그렇지 않다면 락을 잡고 데이터 갱신
     */
    private <T> void dispatchAsyncRefresh(String key, CachePolicy policy, Supplier<T> fetcher, JavaType wrapperType) {
        String markerKey = REFRESH_MARKER_PREFIX + key;
        Boolean markerAcquired = stringRedisTemplate.opsForValue().setIfAbsent(markerKey, "1", policy.lockTtl());
        if (!Boolean.TRUE.equals(markerAcquired)) return;

        Executor executor = executorProvider.getIfAvailable();

        if (executor == null) {
            log.error("비동기 캐시 갱신을 위한 Executor가 존재하지 않음. Key: {}", key);
            stringRedisTemplate.delete(markerKey); // 묶여있던 마커를 풀어줌
            return;
        }

        try {
            executor.execute(() -> {
                String lockKey = LOCK_PREFIX + key;
                String lockValue = UUID.randomUUID().toString();

                // 충돌 방지를 위한 더블 체크
                Boolean lockAcquired = stringRedisTemplate.opsForValue().setIfAbsent(lockKey, lockValue, policy.lockTtl());
                if (!Boolean.TRUE.equals(lockAcquired)) return; // fail fast

                try {
                    CacheWrapper<T> doubleChecked = readCache(key, wrapperType);
                    if (isValid(doubleChecked) && isFresh(doubleChecked, policy.logicalTtlMs())) {
                        return;
                    }
                    fetchAndCache(key, policy.physicalTtl(), fetcher, wrapperType);
                } catch (Exception e) {
                    log.error("비동기 캐시 갱신 실패. Key: {}", key, e);
                } finally {
                    releaseLock(lockKey, lockValue);
                }
            });
        } catch (java.util.concurrent.RejectedExecutionException e) {
            log.warn("스레드 풀 큐가 꽉 차서 캐시 갱신 작업 스킵. Key: {}", key);
            stringRedisTemplate.delete(markerKey);
        } catch (Exception e) {
            log.error("비동기 작업 큐가 꽉 차서 작업 등록 실패.", e);
            stringRedisTemplate.delete(markerKey);
        }

    }

    private <T> T fetchAndCache(String key, Duration physicalTtl, Supplier<T> fetcher, JavaType wrapperType) {
        long requestAt = System.currentTimeMillis();

        T data = fetcher.get(); // 외부에서 데이터를 가져옴
        CacheWrapper<T> wrapper = new CacheWrapper<>(data, requestAt);

        try {
            String existingData = stringRedisTemplate.opsForValue().get(key);
            if (existingData != null) {
                try {
                    CacheWrapper<T> existingWrapper = objectMapper.readValue(existingData, wrapperType);
                    if (existingWrapper.requestedAt() != null && existingWrapper.requestedAt() >= requestAt) {
                        return data;
                    }
                } catch (Exception e) {
                    // ignore
                }
            }
            stringRedisTemplate.opsForValue().set(key, objectMapper.writeValueAsString(wrapper), physicalTtl);
        } catch (Exception e) {
            log.error("데이터 직렬화 및 레디스 저장 실패. key: {}", key, e);
        }
        return data;
    }

    private void releaseLock(String lockKey, String lockValue) {
        try {
            stringRedisTemplate.execute(RELEASE_LOCK_SCRIPT, Collections.singletonList(lockKey), lockValue);
        } catch (Exception e) {
            log.error("레디스 락 해제 실패. Key: {}",lockKey, e);
        }
    }

    private <T> boolean isValid(CacheWrapper<T> wrapper) {
        return wrapper != null && wrapper.data() != null && wrapper.requestedAt() != null;
    }

    private <T> boolean isFresh(CacheWrapper<T> wrapper, long logicalTtlMs) {
        return System.currentTimeMillis() - wrapper.requestedAt() < logicalTtlMs;
    }
}
