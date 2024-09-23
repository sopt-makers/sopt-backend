package org.sopt.app.application.playground.dto;

import java.util.Set;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class RecommendedFriendInfo {

    @Builder
    public record PlaygroundUserFindFilter(
            String key,
            String value
    ) {
    }

    public record PlaygroundUserIds(
            Set<Long> userIds
    ) {
    }
}
