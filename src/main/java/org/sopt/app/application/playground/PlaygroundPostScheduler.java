package org.sopt.app.application.playground;

import java.time.Duration;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.sopt.app.application.appservice.OperationConfigService;
import org.sopt.app.application.playground.dto.PlaygroundPopularPost;
import org.sopt.app.application.playground.dto.PlaygroundRecentPost;
import org.sopt.app.application.playground.dto.PlaygroundRecentPostDto;
import org.sopt.app.common.config.AsyncConfig;
import org.sopt.app.common.config.OperationConfig;
import org.sopt.app.common.config.OperationConfigCategory;
import org.springframework.context.event.EventListener;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class PlaygroundPostScheduler {

    private static final String RECENT_LOCK_KEY = "playground:recent_posts_refresh_lock";
    private static final String POPULAR_LOCK_KEY = "playground:popular_posts_refresh_lock";
    private final PlaygroundAuthService playgroundAuthService;
    private final PlaygroundPostCacheService playgroundPostCacheService;
    private final OperationConfigService operationConfigService;
    private final StringRedisTemplate stringRedisTemplate;

    @Scheduled(cron = "0 0 * * * *")
    public void refreshPlaygroundPosts() {
        log.info("Playground 게시글 캐시 갱신 시작");
        refreshRecentPosts();
        refreshPopularPosts();
        log.info("Playground 게시글 캐시 갱신 완료");
    }

    @Async(AsyncConfig.CACHE_SYNC_EXECUTOR)
    @EventListener(PlaygroundRecentPostRefreshEvent.class)
    public void handleRecentPostRefreshEvent(PlaygroundRecentPostRefreshEvent event) {
        refreshRecentPosts();
    }

    @Async(AsyncConfig.CACHE_SYNC_EXECUTOR)
    @EventListener(PlaygroundPopularPostRefreshEvent.class)
    public void handlePopularPostRefreshEvent(PlaygroundPopularPostRefreshEvent event) {
        refreshPopularPosts();
    }

    private void refreshRecentPosts() {
        Boolean acquired = stringRedisTemplate.opsForValue().setIfAbsent(RECENT_LOCK_KEY, "locked", Duration.ofMinutes(5));
        if (!Boolean.TRUE.equals(acquired)) {
            log.debug("Playground 최신 게시글 캐시 갱신 락 획득 실패");
            return;
        }

        try {
            List<OperationConfig> configList = operationConfigService.getOperationConfigByOperationConfigType(OperationConfigCategory.PLAYGROUND_POST);
            Map<String, String> imageConfigMap = PlaygroundRecentPost.toImageConfigMap(configList);

            List<PlaygroundRecentPost> posts = playgroundAuthService.getPlaygroundRecentPosts().stream()
                .map(post -> PlaygroundRecentPost.of(post, imageConfigMap))
                .toList();
            playgroundPostCacheService.cacheRecentPosts(posts);
            log.info("Playground 최신 게시글 캐시 갱신 완료");
        } catch (Exception e) {
            log.error("Playground 최신 게시글 캐시 갱신 중 오류 발생", e);
        }
    }

    private void refreshPopularPosts() {
        Boolean acquired = stringRedisTemplate.opsForValue().setIfAbsent(POPULAR_LOCK_KEY, "locked", Duration.ofMinutes(5));
        if (!Boolean.TRUE.equals(acquired)) {
            log.debug("Playground 인기 게시글 캐시 갱신 락 획득 실패");
            return;
        }

        try {
            List<PlaygroundPopularPost> posts = playgroundAuthService.getPlaygroundPopularPosts();
            playgroundPostCacheService.cachePopularPosts(posts);
            log.info("Playground 인기 게시글 캐시 갱신 완료");
        } catch (Exception e) {
            log.error("Playground 인기 게시글 캐시 갱신 중 오류 발생", e);
        }
    }
}
