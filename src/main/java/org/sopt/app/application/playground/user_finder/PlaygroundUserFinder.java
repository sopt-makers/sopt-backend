package org.sopt.app.application.playground.user_finder;

import java.util.List;
import java.util.Set;
import org.sopt.app.application.playground.dto.PlaygroundUserFindCondition;

public interface PlaygroundUserFinder {
    List<Long> getPlaygroundUserIdsForSameRecommendType(final PlaygroundUserFindCondition request);
    Set<Long> findByCondition(final PlaygroundUserFindCondition request);
}
