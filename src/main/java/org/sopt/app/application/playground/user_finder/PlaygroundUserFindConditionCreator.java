package org.sopt.app.application.playground.user_finder;

import org.sopt.app.application.playground.dto.PlaygroundProfileInfo.OwnPlaygroundProfile;
import org.sopt.app.application.playground.dto.PlaygroundUserFindCondition;

public interface PlaygroundUserFindConditionCreator {
    PlaygroundUserFindCondition createCondition(OwnPlaygroundProfile profile);
}
