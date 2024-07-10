package org.sopt.app.application.rank;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.sopt.app.application.soptamp.SoptampPointInfo.PartRank;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class RankService {

    private final SoptampPartRankCalculator soptampPartRankCalculator;

    @Transactional(readOnly = true)
    public List<PartRank> findAllPartRanks() {
        return soptampPartRankCalculator.findAllPartRanks();
    }
}
