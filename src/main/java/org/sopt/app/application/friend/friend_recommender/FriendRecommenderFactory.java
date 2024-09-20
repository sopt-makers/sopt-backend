package org.sopt.app.application.friend.friend_recommender;

import org.sopt.app.domain.enums.FriendRecommendType;

public class FriendRecommenderFactory {

    public FriendRecommender create(final FriendRecommendType recommendType) {

        if (recommendType == FriendRecommendType.MBTI) {
            return new MbtiFriendRecommender();
        }

        if (recommendType == FriendRecommendType.GENERATION) {
            return new GenerationFriendRecommender();
        }

        if (recommendType == FriendRecommendType.UNIVERSITY) {
            return new UniversityFriendRecommender();
        }

        throw new IllegalArgumentException("Invalid recommend type");
    }
}
