package org.sopt.app.presentation.home.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RecentPostsResponse {
    private Long id;
    private String title;
    private String category;
    private String content;
    private boolean isHotPost;
}