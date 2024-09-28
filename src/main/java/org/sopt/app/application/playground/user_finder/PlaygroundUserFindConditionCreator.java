package org.sopt.app.application.playground.user_finder;

import java.util.Optional;
import org.sopt.app.application.playground.dto.PlaygroundProfileInfo.OwnPlaygroundProfile;
import org.sopt.app.application.playground.dto.PlaygroundUserFindCondition;

public interface PlaygroundUserFindConditionCreator {
    Optional<PlaygroundUserFindCondition> createCondition(OwnPlaygroundProfile profile);
}
