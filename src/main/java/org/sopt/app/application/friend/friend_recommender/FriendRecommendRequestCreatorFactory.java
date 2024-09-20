package org.sopt.app.application.friend.friend_recommender;

import org.sopt.app.domain.enums.FriendRecommendType;

public class FriendRecommendRequestCreatorFactory {

    public FriendRecommendRequestCreator create(final FriendRecommendType recommendType) {

        if (recommendType == FriendRecommendType.MBTI) {
            return new MbtiFriendRecommendRequestCreator();
        }

        if (recommendType == FriendRecommendType.GENERATION) {
            return new GenerationFriendRecommendRequestCreator();
        }

        if (recommendType == FriendRecommendType.UNIVERSITY) {
            return new UniversityFriendRecommendRequestCreator();
        }

        throw new IllegalArgumentException("Invalid recommend type");
    }
}
