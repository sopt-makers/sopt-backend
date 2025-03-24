package org.sopt.app.presentation.home.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.sopt.app.application.playground.dto.PlaygroundPostInfo.PlaygroundPostResponse;
import org.sopt.app.application.playground.dto.PostWithMemberInfo;

@Builder
@AllArgsConstructor
@Getter
@NoArgsConstructor
public class RecentPostsResponse implements PostWithMemberInfo {
    private Long id;
    private String title;
    private String profileImage;
    private String name;
    private String category;
    private String content;
    private Boolean isHotPost;
    private String url;
    
    
    public static RecentPostsResponse of(PlaygroundPostResponse playgroundPostResponse, String url) {
        return RecentPostsResponse.builder()
                .id(playgroundPostResponse.postId())
                .title(playgroundPostResponse.title())
                .category("HOT")
                .url(url)
                .content(playgroundPostResponse.content())
                .isHotPost(true)
                .build();
    }

    public RecentPostsResponse withUrl(String url) {
        return RecentPostsResponse.builder()
            .id(this.id)
            .title(this.title)
            .profileImage(this.profileImage)
            .name(this.name)
            .category(this.category)
            .content(this.content)
            .isHotPost(this.isHotPost)
            .url(url)
            .build();
    }

    public RecentPostsResponse withMemberDetail(String name, String profileImage) {
        return RecentPostsResponse.builder()
                .id(this.id)
                .title(this.title)
                .profileImage(profileImage)
                .name(name)
                .category(this.category)
                .content(this.content)
                .isHotPost(this.isHotPost)
                .build();
    }

}