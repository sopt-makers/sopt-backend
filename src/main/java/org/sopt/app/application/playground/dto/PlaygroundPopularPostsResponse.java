package org.sopt.app.application.playground.dto;

import java.util.List;

public record PlaygroundPopularPostsResponse(
	List<PlaygroundPopularPost> popularPosts
) {
	public static PlaygroundPopularPostsResponse from(List<PlaygroundPopularPost> hotPosts) {
		return new PlaygroundPopularPostsResponse(hotPosts);
	}
}
