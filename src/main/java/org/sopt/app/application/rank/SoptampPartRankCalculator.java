package org.sopt.app.application.rank;

import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import lombok.RequiredArgsConstructor;
import org.sopt.app.application.soptamp.SoptampPointInfo.PartRank;
import org.sopt.app.application.soptamp.SoptampUserInfo;
import org.sopt.app.domain.enums.Part;


@RequiredArgsConstructor
public class SoptampPartRankCalculator {

    private final List<SoptampUserInfo> userInfos;

    private final Map<String, AtomicInteger> partScores = Map.of(
            Part.SERVER.getPartName(), new AtomicInteger(0),
            Part.WEB.getPartName(), new AtomicInteger(0),
            Part.DESIGN.getPartName(), new AtomicInteger(0),
            Part.ANDROID.getPartName(), new AtomicInteger(0),
            Part.IOS.getPartName(), new AtomicInteger(0),
            Part.PLAN.getPartName(), new AtomicInteger(0)
    );

    private final List<Part> partReturnOrder = List.of(
            Part.PLAN, Part.DESIGN, Part.WEB, Part.IOS, Part.ANDROID, Part.SERVER
    );

    public List<PartRank> calculatePartRank() {
        userInfos.forEach(this::calculatePartScore);
        return partReturnOrder.stream().map(part -> PartRank.builder()
                .part(part.getPartName())
                .rank(getTargetPartRank(partScores.get(part.getPartName()).get()))
                .points(partScores.get(part.getPartName()).longValue())
                .build()).toList();
    }

    private void calculatePartScore(SoptampUserInfo userInfo) {
        String nickname = userInfo.getNickname();
        if(nickname.startsWith(Part.SERVER.getPartName())){
            partScores.get(Part.SERVER.getPartName()).getAndIncrement();
            return;
        }
        if(nickname.startsWith(Part.WEB.getPartName())){
            partScores.get(Part.WEB.getPartName()).getAndIncrement();
            return;
        }
        if(nickname.startsWith(Part.DESIGN.getPartName())){
            partScores.get(Part.DESIGN.getPartName()).getAndIncrement();
            return;
        }
        if(nickname.startsWith(Part.ANDROID.getPartName())){
            partScores.get(Part.ANDROID.getPartName()).getAndIncrement();
            return;
        }
        if(nickname.startsWith(Part.IOS.getPartName())){
            partScores.get(Part.IOS.getPartName()).getAndIncrement();
            return;
        }
        if(nickname.startsWith(Part.PLAN.getPartName())){
            partScores.get(Part.PLAN.getPartName()).getAndIncrement();
            return;
        }
    }

    private int getTargetPartRank(int targetPartPoint) {
        int rankPoint = 1;

        for (AtomicInteger atomicInteger : partScores.values()) {
            if (targetPartPoint < atomicInteger.get()) {
                rankPoint++;
            }
        }
        return rankPoint;
    }
}
