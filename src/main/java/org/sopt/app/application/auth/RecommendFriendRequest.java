package org.sopt.app.application.auth;

import java.util.List;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.ToString;
import org.sopt.app.application.auth.PlaygroundAuthInfo.RecommendFriendFilter;

@Getter
@Builder
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@ToString
public class RecommendFriendRequest {

    private List<Integer> generations;
    private List<RecommendFriendFilter> filters;

    public static RecommendFriendRequest createRecommendFriendRequestByGeneration(List<Integer> generations) {
        return RecommendFriendRequest.builder()
                .generations(generations)
                .filters(List.of())
                .build();
    }
}