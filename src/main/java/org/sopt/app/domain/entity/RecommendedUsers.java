package org.sopt.app.domain.entity;

import javax.persistence.Id;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.sopt.app.application.auth.PlaygroundAuthInfo.PlaygroundUserIds;
import org.sopt.app.application.auth.PlaygroundAuthInfo.RecommendFriendRequest;
import org.springframework.data.redis.core.RedisHash;

@Getter
@AllArgsConstructor(access = AccessLevel.PUBLIC)
@RedisHash(value = "recommendedUsers", timeToLive = 60 * 60 * 24L)
public class RecommendedUsers {
    @Id
    private RecommendFriendRequest request;

    private PlaygroundUserIds playgroundUserIds;
}
