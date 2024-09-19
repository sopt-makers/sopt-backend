package org.sopt.app.application.auth.dto;

import java.util.List;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class RecommendedFriendInfo {

    @Builder
    public record RecommendFriendFilter(
            String key,
            String value
    ) {
    }

    public record PlaygroundUserIds(
            List<Long> userIds
    ) {
    }
}
