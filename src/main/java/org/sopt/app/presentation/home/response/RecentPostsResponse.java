package org.sopt.app.presentation.home.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.sopt.app.application.playground.dto.PlaygroundPostInfo.PlaygroundPostResponse;
import org.sopt.app.application.playground.dto.PostWithMemberInfo;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
public class RecentPostsResponse implements PostWithMemberInfo {
    @Setter
    private Long id;
    private String title;
    private String profileImage;
    private String name;
    private String category;
    private String content;
    private Boolean isHotPost;

    public static RecentPostsResponse of(PlaygroundPostResponse playgroundPostResponse) {
        return RecentPostsResponse.builder()
                .id(playgroundPostResponse.postId())
                .title(playgroundPostResponse.title())
                .category("HOT")
                .content(playgroundPostResponse.content())
                .isHotPost(true)
                .build();
    }

    @Override
    public void setProfileImage(String profileImage) {
        this.profileImage = profileImage;
    }

    @Override
    public void setName(String name) {
        this.name = name;
    }
}