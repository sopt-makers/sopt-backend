package org.sopt.app.facade;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.sopt.app.application.rank.SoptampPartRankCalculator;
import org.sopt.app.application.rank.SoptampUserRankCalculator;
import org.sopt.app.application.soptamp.SoptampPointInfo.Main;
import org.sopt.app.application.soptamp.SoptampPointInfo.PartPoint;
import org.sopt.app.application.soptamp.SoptampPointInfo.PartRank;
import org.sopt.app.application.soptamp.SoptampPointInfo.Point;
import org.sopt.app.application.soptamp.SoptampPointService;
import org.sopt.app.application.soptamp.SoptampUserInfo;
import org.sopt.app.application.soptamp.SoptampUserService;
import org.sopt.app.domain.enums.Part;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class RankFacade {

    private final SoptampUserService soptampUserService;
    private final SoptampPointService soptampPointService;

    @Transactional(readOnly = true)
    @Deprecated
    public List<Main> findAllSoptampUserRanks() {
        List<SoptampUserInfo> soptampUsers = soptampUserService.findAllSoptampUsers();
        SoptampUserRankCalculator soptampUserRankCalculator = new SoptampUserRankCalculator(soptampUsers);
        return soptampUserRankCalculator.calculateRank();
    }

    @Transactional(readOnly = true)
    public List<Main> findCurrentRanks() {
        List<Point> soptampPointList = soptampPointService.findCurrentGenerationPoints();
        List<Long> soptampUserIdList = soptampPointList.stream().map(Point::getSoptampUserId).toList();

        return getMainsByCalculateRanking(soptampUserIdList, soptampPointList);
    }

    private List<Main> getMainsByCalculateRanking(List<Long> soptampUserIdList, List<Point> soptampPointList) {
        List<SoptampUserInfo> userList = soptampUserService.findAllBySoptampUserIds(soptampUserIdList);

        SoptampUserRankCalculator soptampUserRankCalculator = new SoptampUserRankCalculator(userList);
        return soptampUserRankCalculator.calculateRanking(soptampPointList);
    }

    @Transactional(readOnly = true)
    public List<Main> findCurrentRanksByPart(Part part) {
        List<Long> soptampUserIdList = soptampUserService.findSoptampUserByPart(part);
        List<Point> soptampPointList = soptampPointService.findCurrentPointListBySoptampUserIds(soptampUserIdList);

        return getMainsByCalculateRanking(soptampUserIdList, soptampPointList);
    }

    @Transactional(readOnly = true)
    public List<PartRank> findAllPartRanks() {
        List<PartPoint> partPoints = soptampPointService.findSumOfPointAllParts();
        SoptampPartRankCalculator soptampPartRankCalculator = new SoptampPartRankCalculator(partPoints);
        return soptampPartRankCalculator.findAllPartRanks();
    }
}
