package org.sopt.app.application.playground.dto;

import java.util.List;
import org.sopt.app.application.playground.dto.RecommendedFriendInfo.PlaygroundUserFindFilter;

public record PlaygroundUserFindCondition(
        List<Long> generations,
        List<PlaygroundUserFindFilter> filters
) {

    public static PlaygroundUserFindCondition createRecommendFriendRequestByGeneration(List<Long> generations) {
        return new PlaygroundUserFindCondition(generations, List.of());
    }
}
