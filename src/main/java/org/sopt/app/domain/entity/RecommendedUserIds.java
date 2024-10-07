package org.sopt.app.domain.entity;

import java.util.Set;
import lombok.*;
import org.sopt.app.application.playground.dto.PlaygroundUserFindCondition;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;

@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@RedisHash(value = "recommendedUsers", timeToLive = 60 * 60 * 24L)
public class RecommendedUserIds {

    @Id
    private String condition;

    private Set<Long> userIds;

    public RecommendedUserIds(final PlaygroundUserFindCondition request, final Set<Long> userIds) {
        this.condition = request.toString();
        this.userIds = userIds;
    }
}
