package org.sopt.app.presentation.home.response;

import java.util.List;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class EmploymentPostResponse {
    private final Long id;
    private final String categoryName;
    private final String title;
    private final String content;
    private final List<String> images;
}