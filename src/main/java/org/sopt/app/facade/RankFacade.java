package org.sopt.app.facade;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.sopt.app.application.rank.SoptampPartRankCalculator;
import org.sopt.app.application.rank.SoptampUserRankCalculator;
import org.sopt.app.application.soptamp.SoptampPointInfo.Main;
import org.sopt.app.application.soptamp.SoptampPointInfo.PartRank;
import org.sopt.app.application.soptamp.SoptampUserFinder;
import org.sopt.app.application.soptamp.SoptampUserInfo;
import org.sopt.app.domain.enums.Part;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class RankFacade {

    private final SoptampUserFinder soptampUserFinder;

    @Transactional(readOnly = true)
    public List<Main> findCurrentRanks() {
        List<SoptampUserInfo> soptampUserInfos = soptampUserFinder.findAllOfCurrentGeneration();
        SoptampUserRankCalculator soptampUserRankCalculator = new SoptampUserRankCalculator(soptampUserInfos);
        return soptampUserRankCalculator.calculateRank();
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
