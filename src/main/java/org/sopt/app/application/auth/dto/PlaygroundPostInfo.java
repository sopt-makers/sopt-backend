package org.sopt.app.application.auth.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class PlaygroundPostInfo {

    @Getter
    @Builder
    @AllArgsConstructor(access = AccessLevel.PRIVATE)
    public static class PlaygroundPost {

        private String title;
        private String content;
        private String url;
    }

    public record PlaygroundPostResponse(
            @JsonProperty(value = "id")
            Long postId,
            String title,
            String content
    ) {
    }
}
