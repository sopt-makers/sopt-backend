package org.sopt.app.application.platform;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.function.Supplier;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.sopt.app.application.platform.dto.PlatformUserInfoResponse;
import org.sopt.app.application.platform.dto.PlatformUserInfoWrapper;
import org.springframework.test.util.ReflectionTestUtils;

@ExtendWith(MockitoExtension.class)
class PlatformServiceTest {

    @Mock
    private PlatformClient platformClient;
    @Mock
    private PlatformUserCacheService platformUserCacheService;

    @InjectMocks
    private PlatformService platformService;

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(platformService, "apiKey", "test-api-key");
        ReflectionTestUtils.setField(platformService, "serviceName", "test-service");
        ReflectionTestUtils.setField(platformService, "currentGeneration", 34L);
    }

    @Test
    @DisplayName("유저 정보 조회 시 캐시 핸들러를 호출한다")
    void SUCCESS_getPlatformUserInfoResponse_CallsCacheHandler() {
        // given
        Long userId = 1L;
        PlatformUserInfoResponse expectedData = new PlatformUserInfoResponse(1, "testUser", null, null, null, null, 34, List.of());

        when(platformUserCacheService.getPlatformUserInfo(
                eq(userId),
                any(Supplier.class)
        )).thenReturn(expectedData);

        // when
        PlatformUserInfoResponse actualResponse = platformService.getPlatformUserInfoResponse(userId);

        // then
        assertThat(actualResponse).isNotNull();
        assertThat(actualResponse.name()).isEqualTo(expectedData.name());
        verify(platformUserCacheService, times(1)).getPlatformUserInfo(
                eq(userId),
                any(Supplier.class)
        );
    }

    @Test
    @DisplayName("캐시 미스 시 외부 API를 호출하여 데이터를 가져온다")
    void SUCCESS_getPlatformUserInfoResponse_CacheMiss_FetchesFromPlatform() {
        // given
        Long userId = 1L;
        PlatformUserInfoResponse expectedResponse = new PlatformUserInfoResponse(1, "testUser", null, null, null, null, 34, List.of());
        PlatformUserInfoWrapper wrapper = new PlatformUserInfoWrapper(List.of(expectedResponse));

        when(platformUserCacheService.getPlatformUserInfo(
                eq(userId),
                any(Supplier.class)
        )).thenAnswer(invocation -> {
            Supplier<PlatformUserInfoResponse> fetcher = invocation.getArgument(1);
            return fetcher.get();
        });

        when(platformClient.getPlatformUserInfo(any(), any())).thenReturn(wrapper);

        // when
        PlatformUserInfoResponse actualResponse = platformService.getPlatformUserInfoResponse(userId);

        // then
        assertThat(actualResponse).isNotNull();
        assertThat(actualResponse.name()).isEqualTo(expectedResponse.name());
        verify(platformClient, times(1)).getPlatformUserInfo(any(), any());
    }

    @Test
    @DisplayName("유저 기수 목록 조회 시 PlatformUserInfoResponse의 extractGenerationList를 호출한다")
    void SUCCESS_getMemberGenerationList_DelegatesToResponse() {
        // given
        Long userId = 1L;
        PlatformUserInfoResponse profile = new PlatformUserInfoResponse(1, "testUser", null, null, null, null, 34, List.of(
                new PlatformUserInfoResponse.SoptActivities(1, 34, "Server", "Team", true),
                new PlatformUserInfoResponse.SoptActivities(2, 33, "Server", "Team", true)
        ));
        when(platformUserCacheService.getPlatformUserInfo(any(), any())).thenReturn(profile);

        // when
        List<Long> generations = platformService.getMemberGenerationList(userId);

        // then
        assertThat(generations).hasSize(2);
        assertThat(generations.get(0)).isEqualTo(34L);
        assertThat(generations.get(1)).isEqualTo(33L);
    }

    @Test
    @DisplayName("유저의 SOPT 활동 파트를 오래된 기수 순으로 조회한다")
    void SUCCESS_getSoptActivityParts() {
        // given
        PlatformUserInfoResponse profile = new PlatformUserInfoResponse(1, "testUser", null, null, null, null, 34, List.of(
                new PlatformUserInfoResponse.SoptActivities(1, 34, "서버", "Team", true),
                new PlatformUserInfoResponse.SoptActivities(2, 33, "기획", "Team", true),
                new PlatformUserInfoResponse.SoptActivities(3, 35, "서버", "Team", true),
                new PlatformUserInfoResponse.SoptActivities(4, 32, "PM", "Team", false)
        ));

        // when
        String part = platformService.getSoptActivityParts(profile);

        // then
        assertThat(part).isEqualTo("기획/서버");
    }
}
