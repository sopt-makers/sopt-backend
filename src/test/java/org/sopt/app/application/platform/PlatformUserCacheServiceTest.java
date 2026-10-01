package org.sopt.app.application.platform;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.Duration;
import java.util.List;
import java.util.function.Function;
import java.util.function.Supplier;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.sopt.app.application.platform.dto.PlatformUserInfoResponse;
import org.sopt.app.common.cache.CachePolicy;
import org.sopt.app.common.cache.ResilientCacheTemplate;
import org.springframework.core.env.Environment;
import org.springframework.core.env.Profiles;

@ExtendWith(MockitoExtension.class)
class PlatformUserCacheServiceTest {

    @Mock
    private ResilientCacheTemplate resilientCacheTemplate;
    @Mock
    private Environment environment;

    private PlatformUserCacheService platformUserCacheService;

    private final Long userId = 1L;
    private final Long currentGeneration = 34L;
    private final String expectedKey = "platformUserInfo::1";

    @Test
    @DisplayName("SUCCESS_플랫폼 유저 정보 조회 시 템플릿으로 위임 확인")
    void SUCCESS_getPlatformUserInfo_DelegatesToTemplate() {
        // given
        when(environment.acceptsProfiles(any(Profiles.class))).thenReturn(false); // 람다 환경이 아님 -> 비동기 처리 활성화
        platformUserCacheService = new PlatformUserCacheService(resilientCacheTemplate, environment, currentGeneration);

        PlatformUserInfoResponse expectedResponse = userWithLastSoptGeneration(34);
        Supplier<PlatformUserInfoResponse> fetcher = () -> expectedResponse;

        when(resilientCacheTemplate.get(eq(expectedKey), eq(PlatformUserInfoResponse.class), any(CachePolicy.class), eq(fetcher), any()))
            .thenReturn(expectedResponse);

        // when
        PlatformUserInfoResponse result = platformUserCacheService.getPlatformUserInfo(userId, fetcher);

        // then
        assertThat(result).isEqualTo(expectedResponse);

        ArgumentCaptor<CachePolicy> policyCaptor = ArgumentCaptor.forClass(CachePolicy.class);
        verify(resilientCacheTemplate).get(eq(expectedKey), eq(PlatformUserInfoResponse.class), policyCaptor.capture(), eq(fetcher), any());

        CachePolicy capturedPolicy = policyCaptor.getValue();
        assertThat(capturedPolicy.asyncRefreshEnabled()).isTrue();
    }

    @Test
    @DisplayName("SUCCESS_람다 프로필일 때 동기 리프레시 설정 확인")
    void SUCCESS_getPlatformUserInfo_LambdaProfile_SyncRefresh() {
        // given
        when(environment.acceptsProfiles(any(Profiles.class))).thenReturn(true); // 람다 환경임 -> 비동기 처리 비활성화
        platformUserCacheService = new PlatformUserCacheService(resilientCacheTemplate, environment, currentGeneration);

        Supplier<PlatformUserInfoResponse> fetcher = () -> null;

        // when
        platformUserCacheService.getPlatformUserInfo(userId, fetcher);

        // then
        ArgumentCaptor<CachePolicy> policyCaptor = ArgumentCaptor.forClass(CachePolicy.class);
        verify(resilientCacheTemplate).get(eq(expectedKey), eq(PlatformUserInfoResponse.class), policyCaptor.capture(), eq(fetcher), any());

        CachePolicy capturedPolicy = policyCaptor.getValue();
        assertThat(capturedPolicy.asyncRefreshEnabled()).isFalse();
    }

    @Test
    @DisplayName("SUCCESS_현재 기수 활동 유저는 30일 물리 TTL 적용")
    void SUCCESS_resolvePhysicalTtl_ActiveUser_30Days() {
        // given
        when(environment.acceptsProfiles(any(Profiles.class))).thenReturn(false);
        platformUserCacheService = new PlatformUserCacheService(resilientCacheTemplate, environment, currentGeneration);

        PlatformUserInfoResponse activeUser = userWithLastSoptGeneration(34); // 현재 기수와 동일
        platformUserCacheService.getPlatformUserInfo(userId, () -> activeUser);

        // when
        Function<PlatformUserInfoResponse, Duration> resolver = captureResolver();

        // then
        assertThat(resolver.apply(activeUser)).isEqualTo(Duration.ofDays(30));
    }

    @Test
    @DisplayName("SUCCESS_과거 기수 유저는 기본 6시간 물리 TTL 적용")
    void SUCCESS_resolvePhysicalTtl_InactiveUser_6Hours() {
        // given
        when(environment.acceptsProfiles(any(Profiles.class))).thenReturn(false);
        platformUserCacheService = new PlatformUserCacheService(resilientCacheTemplate, environment, currentGeneration);

        PlatformUserInfoResponse inactiveUser = userWithLastSoptGeneration(33); // 과거 기수
        platformUserCacheService.getPlatformUserInfo(userId, () -> inactiveUser);

        // when
        Function<PlatformUserInfoResponse, Duration> resolver = captureResolver();

        // then
        assertThat(resolver.apply(inactiveUser)).isEqualTo(Duration.ofHours(6));
    }

    @Test
    @DisplayName("SUCCESS_솝트 활동 이력이 없는 유저는 기본 6시간 물리 TTL 적용")
    void SUCCESS_resolvePhysicalTtl_NoActivities_6Hours() {
        // given
        when(environment.acceptsProfiles(any(Profiles.class))).thenReturn(false);
        platformUserCacheService = new PlatformUserCacheService(resilientCacheTemplate, environment, currentGeneration);

        PlatformUserInfoResponse noActivityUser =
            new PlatformUserInfoResponse(1, "test", null, null, null, null, 0, null);
        platformUserCacheService.getPlatformUserInfo(userId, () -> noActivityUser);

        // when
        Function<PlatformUserInfoResponse, Duration> resolver = captureResolver();

        // then
        assertThat(resolver.apply(noActivityUser)).isEqualTo(Duration.ofHours(6));
    }

    private PlatformUserInfoResponse userWithLastSoptGeneration(int generation) {
        return new PlatformUserInfoResponse(1, "test", null, null, null, null, generation, List.of(
            new PlatformUserInfoResponse.SoptActivities(1, generation, "서버", "팀", true)
        ));
    }

    @SuppressWarnings("unchecked")
    private Function<PlatformUserInfoResponse, Duration> captureResolver() {
        ArgumentCaptor<Function<PlatformUserInfoResponse, Duration>> resolverCaptor =
            ArgumentCaptor.forClass(Function.class);
        verify(resilientCacheTemplate).get(
            eq(expectedKey), eq(PlatformUserInfoResponse.class), any(CachePolicy.class), any(Supplier.class),
            resolverCaptor.capture());
        return resolverCaptor.getValue();
    }
}
