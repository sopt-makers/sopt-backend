package org.sopt.app.application.rank;

import java.util.AbstractMap;
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

    public Long getUserRank(Long userId) {
        AtomicInteger rankPoint = new AtomicInteger(1);

        return Long.valueOf(soptampUserInfos.stream()
                .sorted(Comparator.comparing(SoptampUserInfo::getTotalPoints).reversed())
                .map(user -> new AbstractMap.SimpleEntry<>(rankPoint.getAndIncrement(), user))
                .filter(entry -> entry.getValue().getId().equals(userId))
                .map(AbstractMap.SimpleEntry::getKey)
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("User not found")));
    }
}
