package org.sopt.app.facade;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import lombok.RequiredArgsConstructor;
import org.sopt.app.application.rank.*;
import org.sopt.app.application.soptamp.SoptampPointInfo.*;
import org.sopt.app.application.soptamp.*;
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
        Set<TypedTuple<Long>> sortedScoreCaches =  rankCacheService.getRanking();
        if (sortedScoreCaches != null && !sortedScoreCaches.isEmpty()) {
            return convertCacheToMain(sortedScoreCaches);
        }
        List<SoptampUserInfo> soptampUserInfos = soptampUserFinder.findAllOfCurrentGeneration();
        rankCacheService.addAll(soptampUserInfos);
        SoptampUserRankCalculator soptampUserRankCalculator = new SoptampUserRankCalculator(soptampUserInfos);
        return soptampUserRankCalculator.calculateRank();
    }

    private List<Main> convertCacheToMain(Set<TypedTuple<Long>> sortedScoreCaches){
        AtomicInteger rankPoint = new AtomicInteger(1);
        return sortedScoreCaches.stream()
                .map(cachedScore -> {
                    CachedUserInfo userInfo = findCachedUserInfo(cachedScore);
                    return Main.builder()
                            .nickname(userInfo.getName())
                            .point((cachedScore.getScore() == null) ? 0L : cachedScore.getScore().longValue())
                            .profileMessage(userInfo.getProfileMessage())
                            .rank(rankPoint.getAndIncrement())
                            .build();
                }).toList();
    }

    private CachedUserInfo findCachedUserInfo(TypedTuple<Long> cachedScore){
        Long id = Long.valueOf(String.valueOf(cachedScore.getValue()));
        CachedUserInfo userInfo = rankCacheService.getUserInfo(id);
        if(userInfo == null){
            SoptampUserInfo soptampUserInfo = soptampUserFinder.findById(id);
            userInfo = CachedUserInfo.of(soptampUserInfo);
            rankCacheService.updateCachedUserInfo(id, userInfo);
        }
        return userInfo;
    }

    @Transactional(readOnly = true)
    public List<Main> findCurrentRanksByPart(Part part) {
        Set<TypedTuple<Long>> sortedScoreCaches =  rankCacheService.getRanking();
        if (sortedScoreCaches != null && !sortedScoreCaches.isEmpty()) {
            return convertCacheToMainByPart(sortedScoreCaches, part);
        }
        List<SoptampUserInfo> soptampUserInfos = soptampUserFinder.findAllByPartAndCurrentGeneration(part);
        SoptampUserRankCalculator soptampUserRankCalculator = new SoptampUserRankCalculator(soptampUserInfos);
        return soptampUserRankCalculator.calculateRank();
    }

    private List<Main> convertCacheToMainByPart(Set<TypedTuple<Long>> sortedScoreCaches, Part part){
        AtomicInteger rankPoint = new AtomicInteger(1);
        return sortedScoreCaches.stream()
                .map(cachedInfo -> Map.entry(findCachedUserInfo(cachedInfo), cachedInfo))
                .filter(entry -> entry.getKey().getPart().equals(part.getPartName()))
                .map(entry -> {
                    CachedUserInfo userInfo = entry.getKey();
                    TypedTuple<Long> cachedScore = entry.getValue();
                    return Main.builder()
                            .nickname(userInfo.getName())
                            .point(cachedScore.getScore() == null ? 0L : cachedScore.getScore().longValue())
                            .profileMessage(userInfo.getProfileMessage())
                            .rank(rankPoint.getAndIncrement())
                            .build();
                })
                .toList();
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
        Set<TypedTuple<Long>> sortedScoreCaches =  rankCacheService.getRanking();
        if (sortedScoreCaches != null && !sortedScoreCaches.isEmpty()) {
            Long rank = 1L;
            for(TypedTuple<Long> cache : sortedScoreCaches){
                if(String.valueOf(cache.getValue()).equals(userId.toString())){
                    return rank;
                }
                rank++;
            }
        }
        List<SoptampUserInfo> soptampUserInfos = soptampUserFinder.findAllOfCurrentGeneration();
        SoptampUserRankCalculator soptampUserRankCalculator = new SoptampUserRankCalculator(soptampUserInfos);
        return soptampUserRankCalculator.getUserRank(userId);
    }
}
