package org.sopt.app.application.rank;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.sopt.app.application.soptamp.SoptampUserInfo;
import org.sopt.app.common.config.CacheType;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ZSetOperations.TypedTuple;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class RedisRankService {
    private final RedisTemplate<String, String> redisRankingTemplate;

    public void createNewRank(String nickname) {
        redisRankingTemplate.opsForZSet().add(CacheType.SOPTAMP_SCORE.getCacheName(), nickname, 0);
    }

    public void removeRank(String nickname) {
        redisRankingTemplate.opsForZSet().remove(CacheType.SOPTAMP_SCORE.getCacheName(), nickname);
    }

    public void incrementScore(String nickname, int score) {
        redisRankingTemplate.opsForZSet().incrementScore(CacheType.SOPTAMP_SCORE.getCacheName(), nickname, score);
    }

    public void decreaseScore(String nickname, int score) {
        redisRankingTemplate.opsForZSet().incrementScore(CacheType.SOPTAMP_SCORE.getCacheName(), nickname, -1 * score);
    }

    public void initScore(String nickname) {
        redisRankingTemplate.opsForZSet().add(CacheType.SOPTAMP_SCORE.getCacheName(), nickname, 0);
    }

    public void deleteAll() {
        redisRankingTemplate.delete(CacheType.SOPTAMP_SCORE.getCacheName());
    }

    public void addAll(List<SoptampUserInfo> userInfos){
        Set<TypedTuple<String>> scores = this.convertRankingSet(userInfos);
        redisRankingTemplate.opsForZSet().add(CacheType.SOPTAMP_SCORE.getCacheName(), scores);
    }

    private Set<TypedTuple<String>> convertRankingSet(List<SoptampUserInfo> userInfos){
        return userInfos.stream()
                .map(user -> TypedTuple.of(user.getNickname(), user.getTotalPoints().doubleValue()))
                .collect(Collectors.toSet());
    }
}
