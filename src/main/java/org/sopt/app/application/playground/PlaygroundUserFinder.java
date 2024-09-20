package org.sopt.app.application.playground;

import java.util.List;
import java.util.Set;
import org.sopt.app.application.playground.dto.PlaygroundUserFindCondition;

public interface PlaygroundUserFinder {
    List<Long> getPlaygroundUserIdsForSameRecommendType(final PlaygroundUserFindCondition request);
    Set<Long> findByRequest(final PlaygroundUserFindCondition request);
}
