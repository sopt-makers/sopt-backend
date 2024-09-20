package org.sopt.app.application.friend;

import java.util.Set;
import org.sopt.app.application.auth.dto.PlaygroundProfileInfo.OwnPlaygroundProfile;
import org.sopt.app.application.friend.friend_recommender.FriendRecommender;
import org.sopt.app.application.friend.friend_recommender.FriendRecommenderFactory;
import org.sopt.app.domain.enums.FriendRecommendType;
import org.springframework.stereotype.Service;

@Service
public class FriendProvider {
    public Set<Long> getFriendPlaygroundIdsByType(OwnPlaygroundProfile profile, FriendRecommendType type) {
        FriendRecommenderFactory factory = new FriendRecommenderFactory();
        FriendRecommender recommender = factory.createRecommender(type);
        return recommender.recommend(profile);
    }
}
