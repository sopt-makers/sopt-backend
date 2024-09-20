package org.sopt.app.application.playground.user_finder;

import java.util.Set;
import lombok.RequiredArgsConstructor;
import org.sopt.app.application.playground.dto.PlaygroundProfileInfo.OwnPlaygroundProfile;
import org.sopt.app.application.playground.dto.PlaygroundUserFindCondition;
import org.sopt.app.domain.enums.FriendRecommendType;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PlaygroundUserIdsProvider {

    private final PlaygroundUserFinder finder;

    public Set<Long> findPlaygroundIdsByType(OwnPlaygroundProfile profile, FriendRecommendType type) {
        PlaygroundUserFindConditionCreatorFactory factory = new PlaygroundUserFindConditionCreatorFactory();
        PlaygroundUserFindConditionCreator conditionCreator = factory.create(type);

        PlaygroundUserFindCondition condition = conditionCreator.createCondition(profile);
        return finder.findByCondition(condition);
    }
}
