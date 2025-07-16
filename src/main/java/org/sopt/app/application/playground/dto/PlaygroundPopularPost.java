package org.sopt.app.application.playground.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public record PlaygroundPopularPost(
	@JsonProperty("id") Long playgroundPostId,
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
		Long postId, String profileImage, String name, String generationAndPart, int rank, String category, String content, String title, String webLink
	) {
		return new PlaygroundPopularPost(
			postId, profileImage, name, generationAndPart, rank, category, title, content, webLink
		);
	}
}
