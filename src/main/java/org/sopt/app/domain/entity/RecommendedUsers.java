package org.sopt.app.domain.entity;

import java.util.List;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.sopt.app.application.auth.dto.RecommendFriendRequest;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;

@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@RedisHash(value = "recommendedUsers", timeToLive = 60 * 60 * 24L)
public class RecommendedUsers {

    @Id
    private String condition;

    private List<Long> userIds;

    public RecommendedUsers(final RecommendFriendRequest request, final List<Long> userIds) {
        this.condition = request.toString();
        this.userIds = userIds;
    }
}
