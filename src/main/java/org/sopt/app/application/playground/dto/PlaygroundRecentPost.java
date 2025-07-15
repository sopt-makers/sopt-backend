package org.sopt.app.application.playground.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

public record PlaygroundRecentPost(
	@JsonProperty("id") Long playgroundPostId,
	String profileImage,
	String name,
	String generationAndPart,
	String category,
	String title,
	String content,
	String webLink,
	@JsonIgnore String createdAt
){
	public static PlaygroundRecentPost from(
		Long postId, String profileImage, String name, String generationAndPart,
		String category, String title, String content, String webLink, String createdAt
	) {
		return new PlaygroundRecentPost(
			postId, profileImage, name, generationAndPart, category, title, content, webLink, createdAt);
	}
}
