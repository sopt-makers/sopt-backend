package org.sopt.app.application.playground;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.TimeUnit;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.sopt.app.application.playground.dto.PlaygroundPopularPost;
import org.sopt.app.application.playground.dto.PlaygroundRecentPost;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class PlaygroundPostCacheService {

    private static final String RECENT_POSTS_KEY = "playground:recent_posts";
    private static final String POPULAR_POSTS_KEY = "playground:popular_posts";
    private static final long CACHE_TTL_SECONDS = 3600L * 24; // 24시간

    private final StringRedisTemplate stringRedisTemplate;
    private final ObjectMapper objectMapper;

    public Optional<List<PlaygroundRecentPost>> getCachedRecentPosts() {
        try {
            String cached = stringRedisTemplate.opsForValue().get(RECENT_POSTS_KEY);
            if (cached == null) {
                return Optional.empty();
            }
            return Optional.of(objectMapper.readValue(cached, new TypeReference<>() {}));
        } catch (Exception e) {
            log.warn("Redis 최근 게시글 캐시 조회 실패", e);
            return Optional.empty();
        }
    }

    public void cacheRecentPosts(List<PlaygroundRecentPost> posts) {
        try {
            stringRedisTemplate.opsForValue().set(
                RECENT_POSTS_KEY,
                objectMapper.writeValueAsString(posts),
                CACHE_TTL_SECONDS, TimeUnit.SECONDS
            );
        } catch (Exception e) {
            log.warn("Redis 최근 게시글 캐시 저장 실패", e);
        }
    }

    public Optional<List<PlaygroundPopularPost>> getCachedPopularPosts() {
        try {
            String cached = stringRedisTemplate.opsForValue().get(POPULAR_POSTS_KEY);
            if (cached == null) {
                return Optional.empty();
            }
            return Optional.of(objectMapper.readValue(cached, new TypeReference<>() {}));
        } catch (Exception e) {
            log.warn("Redis 인기 게시글 캐시 조회 실패", e);
            return Optional.empty();
        }
    }

    public void cachePopularPosts(List<PlaygroundPopularPost> posts) {
        try {
            stringRedisTemplate.opsForValue().set(
                POPULAR_POSTS_KEY,
                objectMapper.writeValueAsString(posts),
                CACHE_TTL_SECONDS, TimeUnit.SECONDS
            );
        } catch (Exception e) {
            log.warn("Redis 인기 게시글 캐시 저장 실패", e);
        }
    }
}
