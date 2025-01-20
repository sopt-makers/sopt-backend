package org.sopt.app.application.rank;

import java.util.List;
import lombok.AllArgsConstructor;
import org.sopt.app.application.soptamp.SoptampUserFinder;
import org.sopt.app.application.soptamp.SoptampUserInfo;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class RankScheduler {
    private final SoptampUserFinder soptampUserFinder;
    private final RedisRankService redisRankService;

    // 매일 오전 4시에 정합성을 맞추기 위해 스케쥴링
    @Scheduled(cron = "0 4 * * * *")
    public void initialSoptampRank(){
        List<SoptampUserInfo> userInfos = soptampUserFinder.findAllOfCurrentGeneration();
        redisRankService.deleteAll();
        redisRankService.addAll(userInfos);
    }
}
