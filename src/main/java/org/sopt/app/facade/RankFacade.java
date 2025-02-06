package org.sopt.app.facade;

import java.util.List;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;
import lombok.RequiredArgsConstructor;
import org.sopt.app.application.rank.CachedUserInfo;
import org.sopt.app.application.rank.RankCacheService;
import org.sopt.app.application.rank.SoptampPartRankCalculator;
import org.sopt.app.application.rank.SoptampUserRankCalculator;
import org.sopt.app.application.soptamp.SoptampPointInfo.Main;
import org.sopt.app.application.soptamp.SoptampPointInfo.PartRank;
import org.sopt.app.application.soptamp.SoptampUserFinder;
import org.sopt.app.application.soptamp.SoptampUserInfo;
import org.sopt.app.domain.enums.Part;
import org.springframework.data.redis.core.ZSetOperations.TypedTuple;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class RankFacade {

    private final SoptampUserFinder soptampUserFinder;
    private final RankCacheService rankCacheService;

    @Transactional(readOnly = true)
    public List<Main> findCurrentRanks() {
        Set<TypedTuple<Long>> cachedRanking =  rankCacheService.getRanking();
        if (cachedRanking != null && !cachedRanking.isEmpty()) {
            return getCachedRanking(cachedRanking);
        }
        List<SoptampUserInfo> soptampUserInfos = soptampUserFinder.findAllOfCurrentGeneration();
        rankCacheService.addAll(soptampUserInfos);
        SoptampUserRankCalculator soptampUserRankCalculator = new SoptampUserRankCalculator(soptampUserInfos);
        return soptampUserRankCalculator.calculateRank();
    }

    private List<Main> getCachedRanking(Set<TypedTuple<Long>> cachedRanking){
        AtomicInteger rankPoint = new AtomicInteger(1);
        return cachedRanking.stream()
                .map(cachedRank -> convertCachedRankingToMain(cachedRank, rankPoint.getAndIncrement()))
                .toList();
    }

    private Main convertCachedRankingToMain(TypedTuple<Long> cachedRanking, Integer rank){
        Long id = Long.valueOf(String.valueOf(cachedRanking.getValue()));
        Long point = (cachedRanking.getScore() != null) ? cachedRanking.getScore().longValue() : 0L;
        CachedUserInfo userInfo = rankCacheService.getUserInfo(id);
        if(userInfo == null){
            SoptampUserInfo soptampUserInfo = soptampUserFinder.findById(id);
            userInfo = CachedUserInfo.of(soptampUserInfo);
            rankCacheService.updateCachedUserInfo(id, userInfo);
        }
        return Main.builder()
                .nickname(userInfo.getName())
                .point(point)
                .profileMessage(userInfo.getProfileMessage())
                .rank(rank)
                .build();
    }

    @Transactional(readOnly = true)
    public List<Main> findCurrentRanksByPart(Part part) {
        List<SoptampUserInfo> soptampUserInfos = soptampUserFinder.findAllByPartAndCurrentGeneration(part);
        SoptampUserRankCalculator soptampUserRankCalculator = new SoptampUserRankCalculator(soptampUserInfos);
        return soptampUserRankCalculator.calculateRank();
    }

    @Transactional(readOnly = true)
    public List<PartRank> findAllPartRanks() {
        List<SoptampUserInfo> soptampUserInfos = soptampUserFinder.findAllOfCurrentGeneration();
        SoptampPartRankCalculator soptampPartRankCalculator = new SoptampPartRankCalculator(soptampUserInfos);
        return soptampPartRankCalculator.calculatePartRank();
    }

    @Transactional(readOnly = true)
    public PartRank findPartRank(Part part) {
        List<SoptampUserInfo> soptampUserInfos = soptampUserFinder.findAllByPartAndCurrentGeneration(part);
        SoptampPartRankCalculator soptampPartRankCalculator = new SoptampPartRankCalculator(soptampUserInfos);
        return soptampPartRankCalculator.calculatePartRank().stream()
                .filter(partRank -> partRank.getPart().equals(part.getPartName()))
                .findFirst().orElseThrow();
    }

    @Transactional(readOnly = true)
    public Long findUserRank(Long userId) {
        List<SoptampUserInfo> soptampUserInfos = soptampUserFinder.findAllOfCurrentGeneration();
        SoptampUserRankCalculator soptampUserRankCalculator = new SoptampUserRankCalculator(soptampUserInfos);
        return soptampUserRankCalculator.getUserRank(userId);
    }
}
