package org.sopt.app.application.playground.user_finder;

import org.sopt.app.domain.enums.FriendRecommendType;

public class PlaygroundUserFindConditionCreatorFactory {

    public PlaygroundUserFindConditionCreator create(final FriendRecommendType recommendType) {

        if (recommendType == FriendRecommendType.MBTI) {
            return new PlaygroundUserFindConditionByMbtiCreator();
        }

        if (recommendType == FriendRecommendType.GENERATION) {
            return new PlaygroundUserFindConditionByGenerationCreator();
        }

        if (recommendType == FriendRecommendType.UNIVERSITY) {
            return new PlaygroundUserFindConditionByUniversityCreator();
        }

        throw new IllegalArgumentException("Invalid recommend type");
    }
}
