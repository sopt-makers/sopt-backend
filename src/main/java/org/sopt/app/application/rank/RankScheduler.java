package org.sopt.app.application.rank;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.sopt.app.application.soptamp.SoptampUserFinder;
import org.sopt.app.application.soptamp.SoptampUserInfo;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class RankScheduler {
    private final SoptampUserFinder soptampUserFinder;
    private final RedisRankService redisRankService;

    @Value("${makers.app.soptamp.appjam-mode:false}")
    private boolean appjamMode;

    // 매일 오전 4시에 정합성을 맞추기 위해 스케쥴링
    @Scheduled(cron = "0 0 4 * * *")
    public void initialSoptampRank(){
        executeSoptampRank();
    }

    public void executeSoptampRank(){
        if (appjamMode) {
            return;
        }
        List<SoptampUserInfo> userInfos = soptampUserFinder.findAllOfCurrentGeneration();
        redisRankService.deleteAll();
        redisRankService.addAll(userInfos);
    }
}
