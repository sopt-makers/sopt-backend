package org.sopt.app.application.playground.dto;

import javax.annotation.Nullable;

import com.fasterxml.jackson.annotation.JsonProperty;

public record PlaygroundPopularPost(
	@JsonProperty("id") Long playgroundPostId,
	@Nullable Long userId,
	String profileImage,
	String name,
	String generationAndPart,
	int rank,
	String category,
	String title,
	String content,
	String webLink
) {
	public static PlaygroundPopularPost from(
		Long postId, Long userId, String profileImage, String name, String generationAndPart, int rank, String category, String content, String title, String webLink
	) {
		return new PlaygroundPopularPost(
			postId, userId, profileImage, name, generationAndPart, rank, category, title, content, webLink
		);
	}
}
