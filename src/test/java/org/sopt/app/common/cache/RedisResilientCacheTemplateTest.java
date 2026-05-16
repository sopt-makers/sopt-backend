package org.sopt.app.common.cache;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.type.TypeFactory;
import java.time.Duration;
import java.util.concurrent.Executor;
import java.util.function.Supplier;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.sopt.app.common.exception.BaseException;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;

@ExtendWith(MockitoExtension.class)
class RedisResilientCacheTemplateTest {

    @Mock
    private StringRedisTemplate stringRedisTemplate;
    @Mock
    private ValueOperations<String, String> valueOperations;
    @Mock
    private ObjectMapper objectMapper;
    @Mock
    private ObjectProvider<Executor> executorProvider;
    @Mock
    private Executor executor;
    @Mock
    private TypeFactory typeFactory;
    @Mock
    private JavaType javaType;

    private RedisResilientCacheTemplate cacheTemplate;

    private final String key = "testKey";
    private final String lockKey = "lock:cache:testKey";
    private final String markerKey = "refresh_marker:cache:testKey";
    private final CachePolicy policy = CachePolicy.of(1000L, Duration.ofHours(1), true);

    @BeforeEach
    void setUp() {
        lenient().when(stringRedisTemplate.opsForValue()).thenReturn(valueOperations);
        lenient().when(objectMapper.getTypeFactory()).thenReturn(typeFactory);
        lenient().when(typeFactory.constructParametricType(eq(RedisResilientCacheTemplate.CacheWrapper.class), any(Class.class)))
            .thenReturn(javaType);
        
        cacheTemplate = new RedisResilientCacheTemplate(stringRedisTemplate, objectMapper, executorProvider);
    }

    @Test
    @DisplayName("SUCCESS_fresh 캐시 히트 시 즉시 데이터 반환")
    void SUCCESS_get_FreshCacheHit() throws Exception {
        // given
        String cachedJson = "{\"data\":\"value\",\"requestedAt\":" + System.currentTimeMillis() + "}";
        when(valueOperations.get(key)).thenReturn(cachedJson);
        
        RedisResilientCacheTemplate.CacheWrapper<String> wrapper = new RedisResilientCacheTemplate.CacheWrapper<>("value", System.currentTimeMillis());
        when(objectMapper.readValue(eq(cachedJson), eq(javaType))).thenReturn(wrapper);

        // when
        String result = cacheTemplate.get(key, String.class, policy, () -> "fetched");

        // then
        assertThat(result).isEqualTo("value");
        verify(valueOperations, never()).setIfAbsent(anyString(), anyString(), any(Duration.class));
    }

    @Test
    @DisplayName("SUCCESS_오래된 캐시 히트 시 데이터 반환 및 비동기 갱신 수행")
    void SUCCESS_get_StaleHit_AsyncRefresh() throws Exception {
        // given
        // 논리적 TTL이 만료된 데이터를 반환하고, 백그라운드에서 갱신 작업을 예약함
        long staleTime = System.currentTimeMillis() - 2000L; // 정책의 논리적 TTL은 1000ms
        String cachedJson = "{\"data\":\"stale\",\"requestedAt\":" + staleTime + "}";
        when(valueOperations.get(key)).thenReturn(cachedJson);
        
        RedisResilientCacheTemplate.CacheWrapper<String> wrapper = new RedisResilientCacheTemplate.CacheWrapper<>("stale", staleTime);
        when(objectMapper.readValue(eq(cachedJson), eq(javaType))).thenReturn(wrapper);
        
        when(valueOperations.setIfAbsent(eq(markerKey), anyString(), any(Duration.class))).thenReturn(true);
        when(executorProvider.getIfAvailable()).thenReturn(executor);

        // when
        String result = cacheTemplate.get(key, String.class, policy, () -> "fetched");

        // then
        assertThat(result).isEqualTo("stale");
        verify(executor).execute(any(Runnable.class));
    }

    @Test
    @DisplayName("SUCCESS_캐시 미스 시 락을 획득하고 데이터를 직접 조회")
    void SUCCESS_get_CacheMiss_BlockingGet() throws Exception {
        // given
        when(valueOperations.get(key)).thenReturn(null);
        when(valueOperations.setIfAbsent(eq(lockKey), anyString(), any(Duration.class))).thenReturn(true);
        when(objectMapper.writeValueAsString(any())).thenReturn("new-json");

        Supplier<String> fetcher = () -> "fetched";

        // when
        String result = cacheTemplate.get(key, String.class, policy, fetcher);

        // then
        assertThat(result).isEqualTo("fetched");
        verify(valueOperations).set(eq(key), eq("new-json"), eq(policy.physicalTtl()));
    }

    @Test
    @DisplayName("SUCCESS_락 획득 타임아웃 발생 시 오래된 데이터라도 반환")
    void SUCCESS_get_Timeout_ReturnStale() throws Exception {
        // given
        // 락 획득에 실패하더라도 캐시에 데이터가 있다면 최악의 상황을 피해 오래된 데이터라도 반환함
        long staleTime = System.currentTimeMillis() - 2000L;
        String cachedJson = "{\"data\":\"stale\",\"requestedAt\":" + staleTime + "}";
        
        // 초기 확인 시 오래된 데이터 반환
        // 루프 확인 시 오래된 데이터 반환
        when(valueOperations.get(key)).thenReturn(cachedJson);
        
        RedisResilientCacheTemplate.CacheWrapper<String> wrapper = new RedisResilientCacheTemplate.CacheWrapper<>("stale", staleTime);
        when(objectMapper.readValue(eq(cachedJson), eq(javaType))).thenReturn(wrapper);
        
        // 락 획득 항상 실패
        when(valueOperations.setIfAbsent(eq(lockKey), anyString(), any(Duration.class))).thenReturn(false);

        CachePolicy shortWaitPolicy = new CachePolicy(1000L, Duration.ofHours(1), false, Duration.ofSeconds(10), Duration.ofMillis(200));

        // when
        String result = cacheTemplate.get(key, String.class, shortWaitPolicy, () -> "fetched");

        // then
        assertThat(result).isEqualTo("stale");
        verify(valueOperations, atLeastOnce()).setIfAbsent(eq(lockKey), anyString(), any(Duration.class));
    }

    @Test
    @DisplayName("FAIL_락 획득 타임아웃 및 캐시 데이터 부재 시 예외 발생")
    void FAIL_get_Timeout_NoStale_ThrowException() throws Exception {
        // given
        when(valueOperations.get(key)).thenReturn(null);
        when(valueOperations.setIfAbsent(eq(lockKey), anyString(), any(Duration.class))).thenReturn(false);

        CachePolicy shortWaitPolicy = new CachePolicy(1000L, Duration.ofHours(1), false, Duration.ofSeconds(10), Duration.ofMillis(100));

        // when & then
        assertThatThrownBy(() -> cacheTemplate.get(key, String.class, shortWaitPolicy, () -> "fetched"))
            .isInstanceOf(BaseException.class)
            .hasMessageContaining("락 획득 시간 초과");
    }

    @Test
    @DisplayName("SUCCESS_역직렬화 실패 시 캐시 미스로 간주하고 데이터 재조회")
    void SUCCESS_get_DeserializationFailure_TreatAsMiss() throws Exception {
        // given
        // 캐시 데이터가 오염되었을 경우 예외를 던지는 대신 미스로 처리하여 자가 치유함
        when(valueOperations.get(key)).thenReturn("corrupted");
        when(objectMapper.readValue(anyString(), eq(javaType))).thenThrow(new RuntimeException("Jackson error"));
        
        when(valueOperations.setIfAbsent(eq(lockKey), anyString(), any(Duration.class))).thenReturn(true);
        when(objectMapper.writeValueAsString(any())).thenReturn("new-json");

        // when
        String result = cacheTemplate.get(key, String.class, policy, () -> "fetched");

        // then
        assertThat(result).isEqualTo("fetched");
        verify(valueOperations).set(eq(key), eq("new-json"), eq(policy.physicalTtl()));
    }

    @Test
    @DisplayName("SUCCESS_블로킹 조회 중 다른 스레드가 캐시를 채우면 해당 데이터 반환")
    void SUCCESS_get_BlockingGet_DoubleCheck() throws Exception {
        // given
        // 락을 획득한 직후 캐시를 다시 확인(Double-check)하여 중복 조회를 방지함
        when(valueOperations.get(key)).thenReturn(null); // 초기 확인
        when(valueOperations.setIfAbsent(eq(lockKey), anyString(), any(Duration.class))).thenReturn(true);
        
        // 락 획득 후, 다른 확인에서 신선한 데이터 반환
        long freshTime = System.currentTimeMillis();
        String freshJson = "{\"data\":\"fresh\",\"requestedAt\":" + freshTime + "}";
        RedisResilientCacheTemplate.CacheWrapper<String> freshWrapper = new RedisResilientCacheTemplate.CacheWrapper<>("fresh", freshTime);
        
        when(valueOperations.get(key))
            .thenReturn(null) // get() 내 readCache
            .thenReturn(null) // blockingGet() 루프 내 readCache
            .thenReturn(freshJson); // 락 획득 후 blockingGet() 내 readCache
            
        when(objectMapper.readValue(eq(freshJson), eq(javaType))).thenReturn(freshWrapper);

        // when
        String result = cacheTemplate.get(key, String.class, policy, () -> "fetched");

        // then
        assertThat(result).isEqualTo("fresh");
        verify(valueOperations, never()).set(eq(key), anyString(), any(Duration.class));
    }

    @Test
    @DisplayName("SUCCESS_비동기 갱신 중 이미 캐시가 갱신되었다면 작업 종료")
    void SUCCESS_dispatchAsyncRefresh_DoubleCheck_Fresh_Exit() throws Exception {
        // given
        long staleTime = System.currentTimeMillis() - 2000L;
        RedisResilientCacheTemplate.CacheWrapper<String> staleWrapper = new RedisResilientCacheTemplate.CacheWrapper<>("stale", staleTime);
        when(valueOperations.get(key)).thenReturn("stale-json");
        when(objectMapper.readValue(eq("stale-json"), eq(javaType))).thenReturn(staleWrapper);
        
        when(valueOperations.setIfAbsent(eq(markerKey), anyString(), any(Duration.class))).thenReturn(true);
        when(executorProvider.getIfAvailable()).thenReturn(executor);
        
        ArgumentCaptor<Runnable> runnableCaptor = ArgumentCaptor.forClass(Runnable.class);
        cacheTemplate.get(key, String.class, policy, () -> "fetched");
        
        verify(executor).execute(runnableCaptor.capture());
        Runnable asyncTask = runnableCaptor.getValue();

        // 비동기 작업을 위한 락 획득 성공 모킹
        when(valueOperations.setIfAbsent(eq(lockKey), anyString(), any(Duration.class))).thenReturn(true);
        
        // 비동기 작업 중 더블 체크 시 신선한 캐시 모킹
        long freshTime = System.currentTimeMillis();
        RedisResilientCacheTemplate.CacheWrapper<String> freshWrapper = new RedisResilientCacheTemplate.CacheWrapper<>("fresh", freshTime);
        when(valueOperations.get(key)).thenReturn("fresh-json");
        when(objectMapper.readValue(eq("fresh-json"), eq(javaType))).thenReturn(freshWrapper);

        Supplier<String> fetcher = mock(Supplier.class);

        // when
        asyncTask.run();

        // then
        verify(fetcher, never()).get();
    }

    @Test
    @DisplayName("SUCCESS_이미 갱신 마커가 존재하면 비동기 갱신 요청 무시")
    void SUCCESS_dispatchAsyncRefresh_MarkerExists_Exit() throws Exception {
        // given
        long staleTime = System.currentTimeMillis() - 2000L;
        RedisResilientCacheTemplate.CacheWrapper<String> staleWrapper = new RedisResilientCacheTemplate.CacheWrapper<>("stale", staleTime);
        when(valueOperations.get(key)).thenReturn("stale-json");
        when(objectMapper.readValue(eq("stale-json"), eq(javaType))).thenReturn(staleWrapper);
        
        // 마커 획득 실패
        when(valueOperations.setIfAbsent(eq(markerKey), anyString(), any(Duration.class))).thenReturn(false);

        // when
        cacheTemplate.get(key, String.class, policy, () -> "fetched");

        // then
        verify(executorProvider, never()).getIfAvailable();
    }

    @Test
    @DisplayName("SUCCESS_비동기 갱신 중 락 획득 실패 시 작업 종료")
    void SUCCESS_dispatchAsyncRefresh_LockContention_Exit() throws Exception {
        // given
        long staleTime = System.currentTimeMillis() - 2000L;
        RedisResilientCacheTemplate.CacheWrapper<String> staleWrapper = new RedisResilientCacheTemplate.CacheWrapper<>("stale", staleTime);
        when(valueOperations.get(key)).thenReturn("stale-json");
        when(objectMapper.readValue(eq("stale-json"), eq(javaType))).thenReturn(staleWrapper);
        
        when(valueOperations.setIfAbsent(eq(markerKey), anyString(), any(Duration.class))).thenReturn(true);
        when(executorProvider.getIfAvailable()).thenReturn(executor);
        
        ArgumentCaptor<Runnable> runnableCaptor = ArgumentCaptor.forClass(Runnable.class);
        cacheTemplate.get(key, String.class, policy, () -> "fetched");
        
        verify(executor).execute(runnableCaptor.capture());
        Runnable asyncTask = runnableCaptor.getValue();

        // 비동기 작업을 위한 락 획득 실패 모킹
        when(valueOperations.setIfAbsent(eq(lockKey), anyString(), any(Duration.class))).thenReturn(false);

        Supplier<String> fetcher = mock(Supplier.class);

        // when
        asyncTask.run();

        // then
        verify(fetcher, never()).get();
    }

    @Test
    @DisplayName("SUCCESS_데이터 저장 전 최신 데이터 존재 여부를 확인하여 덮어쓰기 방지")
    void SUCCESS_fetchAndCache_Fencing_PreventStaleOverwrite() throws Exception {
        // given
        // Java 레벨의 펜싱(Fencing) 메커니즘을 통해 더 최신 데이터가 이미 캐시에 있다면 덮어쓰지 않음
        when(valueOperations.get(key)).thenReturn(null);
        when(valueOperations.setIfAbsent(eq(lockKey), anyString(), any(Duration.class))).thenReturn(true);
        
        // fetchAndCache 중 Redis 다시 확인
        long newerTime = System.currentTimeMillis() + 1000L;
        RedisResilientCacheTemplate.CacheWrapper<String> newerWrapper = new RedisResilientCacheTemplate.CacheWrapper<>("newer", newerTime);
        when(valueOperations.get(key))
            .thenReturn(null) // get() 내 readCache
            .thenReturn(null) // blockingGet() 루프 내 readCache
            .thenReturn(null) // 락 획득 후 blockingGet() 내 readCache
            .thenReturn("newer-json"); // fetchAndCache() 펜싱 체크 내 readCache
            
        when(objectMapper.readValue(eq("newer-json"), eq(javaType))).thenReturn(newerWrapper);

        // when
        String result = cacheTemplate.get(key, String.class, policy, () -> "fetched");

        // then
        assertThat(result).isEqualTo("fetched");
        verify(valueOperations, never()).set(eq(key), anyString(), any(Duration.class));
    }
}
