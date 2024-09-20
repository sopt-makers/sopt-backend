package org.sopt.app.application.playground.user_finder;

import org.sopt.app.application.playground.dto.PlaygroundProfileInfo.OwnPlaygroundProfile;
import org.sopt.app.application.playground.dto.PlaygroundUserFindCondition;

public class PlaygroundUserFindConditionByGenerationCreator implements PlaygroundUserFindConditionCreator {

    @Override
    public PlaygroundUserFindCondition createCondition(OwnPlaygroundProfile profile) {
        return PlaygroundUserFindCondition.createRecommendFriendRequestByGeneration(profile.getAllGenerations());
    }
}
