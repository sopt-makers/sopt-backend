package org.sopt.app.application.friend.friend_recommender;

import org.sopt.app.application.auth.dto.PlaygroundProfileInfo.OwnPlaygroundProfile;
import org.sopt.app.application.auth.dto.RecommendFriendRequest;

public interface FriendRecommendRequestCreator {
    RecommendFriendRequest create(OwnPlaygroundProfile userPlaygroundProfile);
}
