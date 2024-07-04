package org.sopt.app.application.auth;

import java.util.List;
import java.util.Map;

public interface PlaygroundUserRecommender {
    List<Long> getPlaygroundUserIdsForSameRecommendType(
            final Map<String, String> authHeader, final RecommendFriendRequest request);
}
