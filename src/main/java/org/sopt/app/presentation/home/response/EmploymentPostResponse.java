package org.sopt.app.presentation.home.response;

import java.util.List;
import lombok.Builder;
import lombok.Getter;
import org.sopt.app.application.playground.dto.PlayGroundEmploymentResponse.EmploymentPost;

@Getter
@Builder
public class EmploymentPostResponse {
    private final Long id;
    private final String categoryName;
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
}