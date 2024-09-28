package org.sopt.app.application.playground.user_finder;

import java.util.Set;
import org.sopt.app.application.playground.dto.PlaygroundUserFindCondition;

public interface PlaygroundUserFinder {
    Set<Long> findByCondition(final PlaygroundUserFindCondition request);
}
