package org.sopt.app.application.playground;

import java.util.List;
import org.sopt.app.application.auth.dto.RecommendFriendRequest;

public interface PlaygroundUserRecommender {
    List<Long> getPlaygroundUserIdsForSameRecommendType(final RecommendFriendRequest request);
}
