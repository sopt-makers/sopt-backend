package org.sopt.app.application.playground;

import java.time.Duration;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.sopt.app.application.appservice.OperationConfigService;
import org.sopt.app.application.playground.dto.PlaygroundPopularPost;
import org.sopt.app.application.playground.dto.PlaygroundRecentPost;
import org.sopt.app.common.config.OperationConfig;
import org.sopt.app.common.config.OperationConfigCategory;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class PlaygroundPostRefreshService {

    private static final String LOCKED_STATUS = "LOCKED";
    private static final int RECENT_LOCK_MINUTE = 5;
    private static final int POPULAR_LOCK_MINUTE = 5;
    private static final String RECENT_LOCK_KEY = "playground:recent_posts_refresh_lock";
    private static final String POPULAR_LOCK_KEY = "playground:popular_posts_refresh_lock";

    private final PlaygroundAuthService playgroundAuthService;
    private final PlaygroundPostCacheService playgroundPostCacheService;
    private final OperationConfigService operationConfigService;
    private final StringRedisTemplate stringRedisTemplate;

    public void refreshRecentPosts() {
        Boolean acquired = stringRedisTemplate.opsForValue().setIfAbsent(RECENT_LOCK_KEY, LOCKED_STATUS, Duration.ofMinutes(RECENT_LOCK_MINUTE));
        if (!Boolean.TRUE.equals(acquired)) {
            log.error("Playground 최신 게시글 캐시 갱신 락 획득 실패");
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

    public void refreshPopularPosts() {
        Boolean acquired = stringRedisTemplate.opsForValue().setIfAbsent(POPULAR_LOCK_KEY, LOCKED_STATUS, Duration.ofMinutes(POPULAR_LOCK_MINUTE));
        if (!Boolean.TRUE.equals(acquired)) {
            log.error("Playground 인기 게시글 캐시 갱신 락 획득 실패");
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
