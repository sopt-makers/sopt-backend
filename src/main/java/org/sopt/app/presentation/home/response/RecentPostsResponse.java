package org.sopt.app.presentation.home.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.sopt.app.application.playground.dto.PlaygroundPostInfo.PlaygroundPostResponse;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RecentPostsResponse {
    private Long id;
    private String title;
    private String profileImage;
    private String name;
    private String category;
    private String content;
    private boolean isHotPost;

    public static RecentPostsResponse of(PlaygroundPostResponse playgroundPostResponse) {
        return RecentPostsResponse.builder()
                .id(playgroundPostResponse.postId())
                .title(playgroundPostResponse.title())
                .category("HOT")
                .content(playgroundPostResponse.content())
                .isHotPost(true)
                .build();
    }
}