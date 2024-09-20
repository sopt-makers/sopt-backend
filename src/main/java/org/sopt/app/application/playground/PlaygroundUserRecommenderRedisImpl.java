package org.sopt.app.application.playground;

import static org.sopt.app.application.playground.PlaygroundHeaderCreator.createAuthorizationHeaderByInternalPlaygroundToken;

import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.sopt.app.application.auth.dto.RecommendFriendRequest;
import org.sopt.app.application.auth.dto.RecommendedFriendInfo.PlaygroundUserIds;
import org.sopt.app.domain.entity.RecommendedUsers;
import org.sopt.app.interfaces.external.PlaygroundClient;
import org.sopt.app.interfaces.postgres.RecommendedUserIdsRepository;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PlaygroundUserRecommenderRedisImpl implements PlaygroundUserRecommender {

    private final PlaygroundClient playgroundClient;
    private final RecommendedUserIdsRepository recommendedUserIdsRepository;

    @Override
    public List<Long> getPlaygroundUserIdsForSameRecommendType(final RecommendFriendRequest request) {
        Optional<RecommendedUsers> recommendedUsers = recommendedUserIdsRepository.findById(request.toString());

        if (recommendedUsers.isPresent()) {
            return recommendedUsers.get().getUserIds();
        }

        PlaygroundUserIds playgroundUserIds =
                playgroundClient.getPlaygroundUserIdsForSameRecommendType(
                        createAuthorizationHeaderByInternalPlaygroundToken(), request);
        recommendedUserIdsRepository.save(new RecommendedUsers(request, playgroundUserIds.userIds()));
        return playgroundUserIds.userIds();
    }

}