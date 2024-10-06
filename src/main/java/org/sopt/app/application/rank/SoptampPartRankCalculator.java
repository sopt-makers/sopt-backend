package org.sopt.app.application.rank;

import java.util.List;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import org.sopt.app.application.soptamp.SoptampPointInfo.PartRank;
import org.sopt.app.application.soptamp.SoptampUserInfo;
import org.sopt.app.domain.enums.Part;


@RequiredArgsConstructor(access = AccessLevel.PUBLIC)
public class SoptampPartRankCalculator {

    private final List<SoptampUserInfo> userInfos;

    private final PartScores partScores = new PartScores();

    public List<PartRank> calculatePartRank() {
        userInfos.forEach(this::calculatePartScore);
        return Part.getPartsByReturnOrder().stream().map(part -> PartRank.builder()
                .part(part.getPartName())
                .rank(partScores.getRank(part))
                .points(partScores.getPoints(part))
                .build()).toList();
    }

    private void calculatePartScore(SoptampUserInfo userInfo) {
        Part.getAllParts().stream()
                .filter(part -> userInfo.getNickname().startsWith(part.getPartName()))
                .forEach(part -> partScores.addPartScore(part, userInfo.getTotalPoints()));
    }
}
