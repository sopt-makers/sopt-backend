package org.sopt.app.application.playground.user_finder;

import org.sopt.app.domain.enums.FriendRecommendType;

public class PlaygroundUserFindConditionCreatorFactory {

    public PlaygroundUserFindConditionCreator create(final FriendRecommendType recommendType) {

        if (recommendType == FriendRecommendType.MBTI) {
            return new MbtiPlaygroundUserFindConditionCreator();
        }

        if (recommendType == FriendRecommendType.GENERATION) {
            return new GenerationPlaygroundUserFindConditionCreator();
        }

        if (recommendType == FriendRecommendType.UNIVERSITY) {
            return new UniversityPlaygroundUserFindConditionCreator();
        }

        throw new IllegalArgumentException("Invalid recommend type");
    }
}
