package org.sopt.app.facade;

import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.sopt.app.application.rank.SoptampPartRankCalculator;
import org.sopt.app.application.soptamp.SoptampPointInfo.PartRank;
import org.sopt.app.application.soptamp.SoptampPointService;
import org.sopt.app.domain.enums.Part;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class RankFacade {

    private final SoptampPointService soptampPointService;

    @Transactional(readOnly = true)
    public List<PartRank> findAllPartRanks() {
        Map<Part, Long> partPoints = soptampPointService.findSumOfPointAllParts();
        SoptampPartRankCalculator soptampPartRankCalculator = new SoptampPartRankCalculator(partPoints);
        return soptampPartRankCalculator.findAllPartRanks();
    }
}
