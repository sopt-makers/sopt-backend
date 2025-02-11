package org.sopt.app.application.playground.user_finder;

import java.util.Map;
import java.util.Optional;
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
    private final Map<String, PlaygroundUserFindConditionCreator> creatorMap;

    public Set<Long> findPlaygroundIdsByType(OwnPlaygroundProfile profile, FriendRecommendType type) {
        PlaygroundUserFindConditionCreator conditionCreator = creatorMap.get(type.getConditionCreatorBeanName());
        Optional<PlaygroundUserFindCondition> condition = conditionCreator.createCondition(profile);
        if (condition.isPresent()){
            return finder.findByCondition(condition.get());
        }
        return Set.of();
    }
}
