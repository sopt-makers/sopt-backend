package org.sopt.app.application.playground.user_finder;

import java.util.Optional;
import org.sopt.app.application.playground.dto.PlaygroundProfileInfo.OwnPlaygroundProfile;
import org.sopt.app.application.playground.dto.PlaygroundUserFindCondition;

public class PlaygroundUserFindConditionByGenerationCreator implements PlaygroundUserFindConditionCreator {

    @Override
    public Optional<PlaygroundUserFindCondition> createCondition(OwnPlaygroundProfile profile) {
         if (profile.getAllGenerations().isEmpty()) {
             return Optional.empty();
         }
        return Optional.of(
                PlaygroundUserFindCondition.createRecommendFriendRequestByGeneration(profile.getAllGenerations())
        );
    }
}
