package org.sopt.app.application.rank;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.sopt.app.application.soptamp.SoptampPointInfo.PartRank;
import org.sopt.app.application.soptamp.SoptampUserInfo;
import org.sopt.app.domain.enums.Part;


@RequiredArgsConstructor
public class SoptampPartRankCalculator {

    private final List<SoptampUserInfo> userInfos;

    private final HashMap<String, Long> partScores =
            new HashMap<>(Map.of(
            Part.SERVER.getPartName(), 0L,
            Part.WEB.getPartName(), 0L,
            Part.DESIGN.getPartName(), 0L,
            Part.ANDROID.getPartName(), 0L,
            Part.IOS.getPartName(), 0L,
            Part.PLAN.getPartName(), 0L
    ));

    private final List<Part> partReturnOrder = List.of(
            Part.PLAN, Part.DESIGN, Part.WEB, Part.IOS, Part.ANDROID, Part.SERVER
    );

    public List<PartRank> calculatePartRank() {
        userInfos.forEach(this::calculatePartScore);
        return partReturnOrder.stream().map(part -> PartRank.builder()
                .part(part.getPartName())
                .rank(getTargetPartRank(partScores.get(part.getPartName())))
                .points(partScores.get(part.getPartName()))
                .build()).toList();
    }

    private void calculatePartScore(SoptampUserInfo userInfo) {
        String nickname = userInfo.getNickname();
        partReturnOrder.forEach(part -> {
            if(nickname.startsWith(part.getPartName())){
                this.putPartScore(part.getPartName(), userInfo.getTotalPoints());
            }
        });
    }

    private void putPartScore(String partName, Long point) {
        partScores.put(partName, partScores.get(partName) + point);
    }

    private int getTargetPartRank(Long targetPartPoint) {
        int rankPoint = 1;

        for (Long partScore : partScores.values()) {
            if (targetPartPoint < partScore) {
                rankPoint++;
            }
        }
        return rankPoint;
    }
}
