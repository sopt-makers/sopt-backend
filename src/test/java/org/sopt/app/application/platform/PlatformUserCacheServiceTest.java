package org.sopt.app.application.platform;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

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
    private final String expectedKey = "platformUserInfo::1";

    @Test
    @DisplayName("SUCCESS_플랫폼 유저 정보 조회 시 템플릿으로 위임 확인")
    void SUCCESS_getPlatformUserInfo_DelegatesToTemplate() {
        // given
        when(environment.acceptsProfiles(any(Profiles.class))).thenReturn(false); // 람다 환경이 아님 -> 비동기 처리 활성화
        platformUserCacheService = new PlatformUserCacheService(resilientCacheTemplate, environment);

        PlatformUserInfoResponse expectedResponse = new PlatformUserInfoResponse(1, "test", null, null, null, null, 34, null);
        Supplier<PlatformUserInfoResponse> fetcher = () -> expectedResponse;

        when(resilientCacheTemplate.get(eq(expectedKey), eq(PlatformUserInfoResponse.class), any(CachePolicy.class), eq(fetcher)))
            .thenReturn(expectedResponse);

        // when
        PlatformUserInfoResponse result = platformUserCacheService.getPlatformUserInfo(userId, fetcher);

        // then
        assertThat(result).isEqualTo(expectedResponse);

        ArgumentCaptor<CachePolicy> policyCaptor = ArgumentCaptor.forClass(CachePolicy.class);
        verify(resilientCacheTemplate).get(eq(expectedKey), eq(PlatformUserInfoResponse.class), policyCaptor.capture(), eq(fetcher));

        CachePolicy capturedPolicy = policyCaptor.getValue();
        assertThat(capturedPolicy.asyncRefreshEnabled()).isTrue();
    }

    @Test
    @DisplayName("SUCCESS_람다 프로필일 때 동기 리프레시 설정 확인")
    void SUCCESS_getPlatformUserInfo_LambdaProfile_SyncRefresh() {
        // given
        when(environment.acceptsProfiles(any(Profiles.class))).thenReturn(true); // 람다 환경임 -> 비동기 처리 비활성화
        platformUserCacheService = new PlatformUserCacheService(resilientCacheTemplate, environment);

        Supplier<PlatformUserInfoResponse> fetcher = () -> null;

        // when
        platformUserCacheService.getPlatformUserInfo(userId, fetcher);

        // then
        ArgumentCaptor<CachePolicy> policyCaptor = ArgumentCaptor.forClass(CachePolicy.class);
        verify(resilientCacheTemplate).get(eq(expectedKey), eq(PlatformUserInfoResponse.class), policyCaptor.capture(), eq(fetcher));

        CachePolicy capturedPolicy = policyCaptor.getValue();
        assertThat(capturedPolicy.asyncRefreshEnabled()).isFalse();
    }
}
