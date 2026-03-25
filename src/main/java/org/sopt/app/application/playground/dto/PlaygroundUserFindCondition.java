package org.sopt.app.application.playground.dto;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.sopt.app.application.playground.dto.RecommendedFriendInfo.PlaygroundUserFindFilter;

public record PlaygroundUserFindCondition(
        Set<Long> generations,
        List<PlaygroundUserFindFilter> filters
) {
    // 기존에 List였던 generations를 Set으로 변경하며 대응을 위해 생성자를 오버라이딩하여 사용함
    public PlaygroundUserFindCondition(List<Long> generationsList, List<PlaygroundUserFindFilter> filters) {
        this(new HashSet<>(generationsList), filters);
    }

    public static PlaygroundUserFindCondition createRecommendFriendRequestByGeneration(List<Long> generations) {
        return new PlaygroundUserFindCondition(new HashSet<>(generations), List.of());
    }
}
