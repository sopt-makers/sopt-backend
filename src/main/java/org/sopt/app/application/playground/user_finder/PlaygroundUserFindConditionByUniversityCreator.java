package org.sopt.app.application.playground.user_finder;

import static org.sopt.app.domain.enums.FriendRecommendType.UNIVERSITY;

import java.util.List;
import java.util.Optional;
import org.sopt.app.application.playground.dto.PlaygroundProfileInfo.OwnPlaygroundProfile;
import org.sopt.app.application.playground.dto.PlaygroundUserFindCondition;
import org.sopt.app.application.playground.dto.RecommendedFriendInfo.PlaygroundUserFindFilter;
import org.springframework.stereotype.Component;

@Component
public class PlaygroundUserFindConditionByUniversityCreator implements PlaygroundUserFindConditionCreator {

    @Override
    public Optional<PlaygroundUserFindCondition> createCondition(OwnPlaygroundProfile profile) {
        if (profile.getUniversity() == null) {
            return Optional.empty();
        }
        List<PlaygroundUserFindFilter> filter = List.of(
                PlaygroundUserFindFilter.builder()
                        .key(String.valueOf(UNIVERSITY))
                        .value(profile.getUniversity())
                        .build());
        return Optional.of(new PlaygroundUserFindCondition(profile.getAllGenerations(), filter));
    }

}
