package org.sopt.app.application.playground.user_finder;

import static org.sopt.app.domain.enums.FriendRecommendType.UNIVERSITY;

import java.util.List;
import org.sopt.app.application.playground.dto.PlaygroundProfileInfo.OwnPlaygroundProfile;
import org.sopt.app.application.playground.dto.PlaygroundUserFindCondition;
import org.sopt.app.application.playground.dto.RecommendedFriendInfo.PlaygroundUserFindFilter;

public class PlaygroundUserFindConditionByUniversityCreator implements PlaygroundUserFindConditionCreator {

    @Override
    public PlaygroundUserFindCondition createCondition(OwnPlaygroundProfile profile) {
        List<PlaygroundUserFindFilter> filter = List.of(
                PlaygroundUserFindFilter.builder()
                        .key(String.valueOf(UNIVERSITY))
                        .value(profile.getUniversity())
                        .build());
        return new PlaygroundUserFindCondition(profile.getAllGenerations(), filter);
    }

}
