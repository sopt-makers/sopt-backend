package org.sopt.app.application.playground;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.Duration;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.sopt.app.application.appservice.OperationConfigService;
import org.sopt.app.application.playground.dto.PlaygroundPopularPost;
import org.sopt.app.application.playground.dto.PlaygroundRecentPostDto;
import org.sopt.app.common.config.OperationConfig;
import org.sopt.app.common.config.OperationConfigCategory;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;

@ExtendWith(MockitoExtension.class)
class PlaygroundPostRefreshServiceTest {

    @Mock
    private PlaygroundAuthService playgroundAuthService;
    @Mock
    private PlaygroundPostCacheService playgroundPostCacheService;
    @Mock
    private OperationConfigService operationConfigService;
    @Mock
    private StringRedisTemplate stringRedisTemplate;
    @Mock
    private ValueOperations<String, String> valueOperations;
    @Mock
    private OperationConfig operationConfig;

    @InjectMocks
    private PlaygroundPostRefreshService playgroundPostRefreshService;

    @Test
    @DisplayName("최신 게시글 갱신 - 락 획득 성공 시 캐시 갱신 수행")
    void SUCCESS_refreshRecentPosts() {
        // given
        when(stringRedisTemplate.opsForValue()).thenReturn(valueOperations);
        when(valueOperations.setIfAbsent(anyString(), anyString(), any(Duration.class))).thenReturn(true);
        when(operationConfigService.getOperationConfigByOperationConfigType(OperationConfigCategory.PLAYGROUND_POST)).thenReturn(List.of(operationConfig));
        when(operationConfig.getKey()).thenReturn("자유.imageUrl");
        when(operationConfig.getValue()).thenReturn("http://image.url");

        PlaygroundRecentPostDto postDto = new PlaygroundRecentPostDto(
            1L, 1L, "profile", "name", "33,서버", "자유", "제목", "내용", "link", "2024-05-14 00:00:00.000000"
        );
        when(playgroundAuthService.getPlaygroundRecentPosts()).thenReturn(List.of(postDto));

        // when
        playgroundPostRefreshService.refreshRecentPosts();

        // then
        verify(playgroundAuthService, times(1)).getPlaygroundRecentPosts();
        verify(playgroundPostCacheService, times(1)).cacheRecentPosts(any());
    }

    @Test
    @DisplayName("최신 게시글 갱신 - 락 획득 실패 시 캐시 갱신 미수행")
    void FAIL_refreshRecentPosts_LockAcquisition() {
        // given
        when(stringRedisTemplate.opsForValue()).thenReturn(valueOperations);
        when(valueOperations.setIfAbsent(anyString(), anyString(), any(Duration.class))).thenReturn(false);

        // when
        playgroundPostRefreshService.refreshRecentPosts();

        // then
        verify(playgroundAuthService, never()).getPlaygroundRecentPosts();
        verify(playgroundPostCacheService, never()).cacheRecentPosts(any());
    }

    @Test
    @DisplayName("인기 게시글 갱신 - 락 획득 성공 시 캐시 갱신 수행")
    void SUCCESS_refreshPopularPosts() {
        // given
        when(stringRedisTemplate.opsForValue()).thenReturn(valueOperations);
        when(valueOperations.setIfAbsent(anyString(), anyString(), any(Duration.class))).thenReturn(true);
        
        PlaygroundPopularPost popularPost = new PlaygroundPopularPost(
            1L, 1L, "profile", "name", "33,서버", 1, "자유", "제목", "내용", "link"
        );
        when(playgroundAuthService.getPlaygroundPopularPosts()).thenReturn(List.of(popularPost));

        // when
        playgroundPostRefreshService.refreshPopularPosts();

        // then
        verify(playgroundAuthService, times(1)).getPlaygroundPopularPosts();
        verify(playgroundPostCacheService, times(1)).cachePopularPosts(any());
    }

    @Test
    @DisplayName("인기 게시글 갱신 - 락 획득 실패 시 캐시 갱신 미수행")
    void FAIL_refreshPopularPosts_LockAcquisition() {
        // given
        when(stringRedisTemplate.opsForValue()).thenReturn(valueOperations);
        when(valueOperations.setIfAbsent(anyString(), anyString(), any(Duration.class))).thenReturn(false);

        // when
        playgroundPostRefreshService.refreshPopularPosts();

        // then
        verify(playgroundAuthService, never()).getPlaygroundPopularPosts();
        verify(playgroundPostCacheService, never()).cachePopularPosts(any());
    }
}
