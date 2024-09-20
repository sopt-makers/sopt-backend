package org.sopt.app.application.playground.user_finder;

import static org.sopt.app.application.playground.PlaygroundHeaderCreator.createAuthorizationHeaderByInternalPlaygroundToken;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import org.sopt.app.application.playground.PlaygroundClient;
import org.sopt.app.application.playground.dto.PlaygroundUserFindCondition;
import org.sopt.app.application.playground.dto.RecommendedFriendInfo.PlaygroundUserIds;
import org.sopt.app.domain.entity.RecommendedUserIds;
import org.sopt.app.interfaces.postgres.RecommendedUserIdsRepository;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PlaygroundUserFinderRedisImpl implements PlaygroundUserFinder {

    private final PlaygroundClient playgroundClient;
    private final RecommendedUserIdsRepository recommendedUserIdsRepository;

    @Override
    @Deprecated
    public List<Long> getPlaygroundUserIdsForSameRecommendType(final PlaygroundUserFindCondition request) {
        return List.of();
    }

    @Override
    public Set<Long> findByCondition(PlaygroundUserFindCondition request) {
        Optional<RecommendedUserIds> recommendedUsers = recommendedUserIdsRepository.findById(request.toString());

        if (recommendedUsers.isPresent()) {
            return recommendedUsers.get().getUserIds();
        }

        PlaygroundUserIds playgroundUserIds =
                playgroundClient.getPlaygroundUserIdsForSameRecommendType(
                        createAuthorizationHeaderByInternalPlaygroundToken(), request);
        recommendedUserIdsRepository.save(new RecommendedUserIds(request, playgroundUserIds.userIds()));
        return playgroundUserIds.userIds();
    }

}
