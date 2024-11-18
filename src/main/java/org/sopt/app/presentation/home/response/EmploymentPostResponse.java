package org.sopt.app.presentation.home.response;

import java.util.List;
import lombok.Builder;
import lombok.Getter;
import org.sopt.app.application.playground.dto.PlayGroundEmploymentResponse.EmploymentPost;
import org.sopt.app.application.playground.dto.PostWithMemberInfo;

@Getter
@Builder
public class EmploymentPostResponse implements PostWithMemberInfo {
    private final Long id;
    private final String categoryName;
    private String profileImage;
    private String name;
    private final String title;
    private final String content;
    private final List<String> images;

    public static EmploymentPostResponse of(EmploymentPost employmentPost) {
        return EmploymentPostResponse.builder()
                .id(employmentPost.id())
                .categoryName(employmentPost.categoryName())
                .title(employmentPost.title())
                .content(employmentPost.content())
                .images(employmentPost.images())
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