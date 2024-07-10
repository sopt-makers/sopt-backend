package org.sopt.app.facade;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.val;
import org.sopt.app.application.rank.SoptampPartRankCalculator;
import org.sopt.app.application.slack.SlackService;
import org.sopt.app.application.soptamp.SoptampPointInfo;
import org.sopt.app.application.soptamp.SoptampPointInfo.Main;
import org.sopt.app.application.soptamp.SoptampPointInfo.PartRank;
import org.sopt.app.application.soptamp.SoptampPointInfo.Point;
import org.sopt.app.application.soptamp.SoptampPointService;
import org.sopt.app.application.soptamp.SoptampUserService;
import org.sopt.app.domain.entity.SoptampUser;
import org.sopt.app.domain.enums.Part;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class RankFacade {

    private final SoptampUserService soptampUserService;
    private final SoptampPointService soptampPointService;

    @Transactional(readOnly = true)
    public List<Main> findAllSoptampUserRanks() {
        List<SoptampUser> soptampUsers = soptampUserService.findAllSoptampUsers();
        return this.getRanking(soptampUsers);
    }

    @Transactional(readOnly = true)
    public List<Main> findCurrentRanksByPart() {
        val soptampPointList = soptampPointService.findCurrentPointList();
        return this.findCurrentRanks(soptampPointList);
    }

    @Transactional(readOnly = true)
    public List<Main> findCurrentRanksByPart(Part part) {
        val soptampUserIdList = soptampUserService.findSoptampUserByPart(part);
        val soptampPointList = soptampPointService.findCurrentPointListBySoptampUserIds(soptampUserIdList);
        return this.findCurrentRanks(soptampPointList);
    }

    @Transactional(readOnly = true)
    public List<PartRank> findAllPartRanks() {
        Map<Part, Long> partPoints = soptampPointService.findSumOfPointAllParts();
        SoptampPartRankCalculator soptampPartRankCalculator = new SoptampPartRankCalculator(partPoints);
        return soptampPartRankCalculator.findAllPartRanks();
    }

    private List<SoptampPointInfo.Main> findCurrentRanks(List<Point> soptampPointList) {
        val soptampUserIdList = soptampPointList.stream().map(Point::getSoptampUserId).toList();
        val userList = soptampUserService.findAllBySoptampUserIds(soptampUserIdList);
        return this.getCurrentRanking(userList, soptampPointList);
    }

    private List<Main> getCurrentRanking(List<SoptampUser> userList, List<Point> soptampPointList) {
        val rankPoint = new AtomicInteger(1);
        List<Main> rankingList = new ArrayList<>();

        soptampPointList.stream().sorted(Comparator.comparing(Point::getPoints).reversed())
                .forEach(point -> userList.stream()
                        .filter(user -> user.getId().equals(point.getSoptampUserId()))
                        .findAny()
                        .ifPresentOrElse(
                                user -> rankingList.add(Main.builder()
                                        .rank(rankPoint.getAndIncrement())
                                        .nickname(user.getNickname())
                                        .point(point.getPoints())
                                        .profileMessage(user.getProfileMessage())
                                        .build()),
                                () -> SlackService.sendSlackMessage(
                                        "Warning",
                                        "soptamp_point에 해당하지 않는 soptamp_user가 확인되었습니다.\n"
                                                + "soptampPointId: " + point.getId() + "\n"
                                                + "soptampUserId: " + point.getSoptampUserId()
                                )
                        ));

        return rankingList;
    }

    private List<Main> getRanking(List<SoptampUser> userList) {
        val rankPoint = new AtomicInteger(1);
        return userList.stream().sorted(
                        Comparator.comparing(SoptampUser::getTotalPoints).reversed())
                .map(user -> Main.builder()
                        .rank(rankPoint.getAndIncrement())
                        .nickname(user.getNickname())
                        .point(user.getTotalPoints())
                        .profileMessage(user.getProfileMessage())
                        .build())
                .collect(Collectors.toList());
    }
}
