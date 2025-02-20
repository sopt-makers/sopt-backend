package org.sopt.app.application.rank;

import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.sopt.app.application.soptamp.SoptampUserInfo;
import org.sopt.app.common.config.CacheType;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ZSetOperations.TypedTuple;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class RedisRankService implements RankCacheService{
    private final RedisTemplate<String, Long> redisTemplate;

    @Override
    public Set<TypedTuple<Long>> getRanking() {
        try {
            return redisTemplate.opsForZSet()
                    .reverseRangeWithScores(CacheType.SOPTAMP_SCORE.getCacheName(), 0, -1);
        }catch (Exception e){
            log.warn("Redis에서 Soptamp 랭킹 조회 중 오류 발생", e);
            return Collections.emptySet();
        }
    }

    @Override
    public void createNewRank(Long userId) {
        redisTemplate.opsForZSet().add(CacheType.SOPTAMP_SCORE.getCacheName(), userId, 0);
    }

    @Override
    public void removeRank(Long userId) {
        redisTemplate.opsForZSet().remove(CacheType.SOPTAMP_SCORE.getCacheName(), userId);
    }

    @Override
    public void incrementScore(Long userId, int score) {
        redisTemplate.opsForZSet()
                .incrementScore(CacheType.SOPTAMP_SCORE.getCacheName(), userId, score);
    }

    @Override
    public void decreaseScore(Long userId, int score) {
        redisTemplate.opsForZSet()
                .incrementScore(CacheType.SOPTAMP_SCORE.getCacheName(), userId, -1 * score);
    }

    @Override
    public void initScore(Long userId) {
        redisTemplate.opsForZSet()
                .add(CacheType.SOPTAMP_SCORE.getCacheName(), userId, 0);
    }

    @Override
    public void deleteAll() {
        redisTemplate.delete(CacheType.SOPTAMP_SCORE.getCacheName());
    }

    @Override
    public void updateCachedUserInfo(Long id, CachedUserInfo userInfo){
        redisTemplate.opsForHash()
                .put(CacheType.SOPTAMP_PROFILE_MESSAGE.getCacheName(), id, userInfo);
    }

    @Override
    public CachedUserInfo getUserInfo(Long id) {
        try {
            return (CachedUserInfo) redisTemplate.opsForHash()
                    .get(CacheType.SOPTAMP_PROFILE_MESSAGE.getCacheName(), id);
        } catch (Exception e) {
            log.warn("Redis에서 프로필 메시지를 가져오는 중 오류 발생 (userId: {})", id, e);
            return null;
        }
    }

    @Override
    public void addAll(List<SoptampUserInfo> userInfos) {
        try {
            Set<TypedTuple<Long>> scores = this.convertRankingSet(userInfos);
            redisTemplate.opsForZSet().add(CacheType.SOPTAMP_SCORE.getCacheName(), scores);
        } catch (Exception e) {
            log.warn("Redis에 랭킹 데이터를 추가하는 중 오류 발생", e);
        }
    }

    private Set<TypedTuple<Long>> convertRankingSet(List<SoptampUserInfo> userInfos){
        return userInfos.stream()
                .map(user -> TypedTuple.of(user.getUserId(), user.getTotalPoints().doubleValue()))
                .collect(Collectors.toSet());
    }
}
