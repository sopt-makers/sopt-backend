package org.sopt.app.application.rank;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.sopt.app.application.slack.SlackService;
import org.sopt.app.application.soptamp.SoptampPointInfo.Main;
import org.sopt.app.application.soptamp.SoptampPointInfo.Point;
import org.sopt.app.application.soptamp.SoptampUserInfo;

@RequiredArgsConstructor
public class SoptampUserRankCalculator {

    private final List<SoptampUserInfo> soptampUsers;
    private final List<Main> ranking = new ArrayList<>();
    private int rankPoint = 1;

    public List<Main> calculateRanking(List<Point> soptampPointList) {
        soptampPointList.stream()
                .sorted(Comparator.comparing(Point::getPoints).reversed())
                .forEach(point ->
                        findSoptampUserInfo(point.getSoptampUserId())
                        .ifPresentOrElse(
                                user -> addUserToRanking(user, point.getPoints()),
                                () -> SlackService.sendSlackMessage(
                                        "Warning",
                                        "soptamp_point에 해당하지 않는 soptamp_user가 확인되었습니다.\n"
                                                + "soptampPointId: " + point.getId() + "\n"
                                                + "soptampUserId: " + point.getSoptampUserId()
                                )
                        ));
        return ranking;
    }

    private void addUserToRanking(SoptampUserInfo user, Long userPoint) {
        ranking.add(Main.builder()
                .rank(getCurrentRankPointAndIncrement())
                .nickname(user.getNickname())
                .profileMessage(user.getProfileMessage())
                .point(userPoint)
                .build());
    }

    private Optional<SoptampUserInfo> findSoptampUserInfo(Long soptampUserId) {
        return soptampUsers.stream()
                .filter(user -> user.getId().equals(soptampUserId))
                .findAny();
    }

    private int getCurrentRankPointAndIncrement() {
        return rankPoint++;
    }

    @Deprecated
    public List<Main> calculateRank() {
        return soptampUsers.stream().sorted(
                        Comparator.comparing(SoptampUserInfo::getTotalPoints).reversed())
                .map(user -> Main.of(getCurrentRankPointAndIncrement(), user))
                .collect(Collectors.toList());
    }
}
