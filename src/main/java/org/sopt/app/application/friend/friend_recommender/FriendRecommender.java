package org.sopt.app.application.friend.friend_recommender;

import java.util.Set;
import org.sopt.app.application.auth.dto.PlaygroundProfileInfo.OwnPlaygroundProfile;

public interface FriendRecommender {
    Set<Long> recommend(OwnPlaygroundProfile userPlaygroundProfile);

}
