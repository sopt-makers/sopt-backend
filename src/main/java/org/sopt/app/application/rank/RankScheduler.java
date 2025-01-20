package org.sopt.app.application.rank;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.AllArgsConstructor;
import org.sopt.app.application.soptamp.SoptampPointInfo.Main;
import org.sopt.app.application.soptamp.SoptampUserFinder;
import org.sopt.app.application.soptamp.SoptampUserInfo;
import org.sopt.app.common.config.CacheType;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ZSetOperations.TypedTuple;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class RankScheduler {
    private final SoptampUserFinder soptampUserFinder;
    private final RedisTemplate<String, String> redisRankingTemplate;

    // 매일 오전 4시에 정합성을 맞추기 위해 스케쥴링
    @Scheduled(cron = "0 4 * * * *")
    public void initialSoptampRank(){
        List<SoptampUserInfo> userInfos = soptampUserFinder.findAllOfCurrentGeneration();
        Set<TypedTuple<String>> soptampScores = this.convertRankingSet(userInfos);
        redisRankingTemplate.opsForZSet().removeRange(CacheType.SOPTAMP_SCORE.getCacheName(), 0, -1);
        redisRankingTemplate.opsForZSet().add(
                CacheType.SOPTAMP_SCORE.getCacheName(),
                soptampScores
        );
    }

    private Set<TypedTuple<String>> convertRankingSet(List<SoptampUserInfo> userInfos){
        return userInfos.stream()
                .map(user -> TypedTuple.of(user.getNickname(), user.getTotalPoints().doubleValue()))
                .collect(Collectors.toSet());
    }
}
