package org.sopt.app.application.auth.dto;

import java.util.List;
import org.sopt.app.application.auth.dto.RecommendedFriendInfo.RecommendFriendFilter;

public record RecommendFriendRequest(
        List<Integer> generations,
        List<RecommendFriendFilter> filters
) {

    public static RecommendFriendRequest createRecommendFriendRequestByGeneration(List<Integer> generations) {
        return new RecommendFriendRequest(generations, List.of());
    }
}
