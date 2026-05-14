package org.sopt.app.application.playground.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import javax.annotation.Nullable;

public record PlaygroundRecentPostDto(
    @JsonProperty("id") Long id,
    @Nullable Long userId,
    String profileImage,
    String name,
    String generationAndPart,
    String category,
    String title,
    String content,
    String webLink,
    String createdAt
) {
}
