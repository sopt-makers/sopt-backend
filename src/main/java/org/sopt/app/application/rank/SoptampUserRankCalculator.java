package org.sopt.app.application.rank;

import java.util.Comparator;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import lombok.RequiredArgsConstructor;
import org.sopt.app.application.soptamp.SoptampPointInfo.Main;
import org.sopt.app.application.soptamp.SoptampUserInfo;

@RequiredArgsConstructor
public class SoptampUserRankCalculator {

    private final List<SoptampUserInfo> soptampUserInfos;

    public List<Main> calculateRank() {
        AtomicInteger rankPoint = new AtomicInteger(1);

        return soptampUserInfos.stream()
                .sorted(Comparator.comparing(SoptampUserInfo::getTotalPoints).reversed())
                .map(user -> Main.of(rankPoint.getAndIncrement(), user))
                .toList();
    }
}
