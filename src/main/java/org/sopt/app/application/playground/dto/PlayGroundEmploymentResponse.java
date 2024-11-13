package org.sopt.app.application.playground.dto;

import java.util.List;

public record PlayGroundEmploymentResponse(
        List<EmploymentPost> posts
) {
    public record EmploymentPost(
            Long id,
            String categoryName,
            String title,
            String content,
            List<String> images
    ) {
    }
}