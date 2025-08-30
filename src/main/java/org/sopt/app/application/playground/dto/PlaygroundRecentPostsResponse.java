package org.sopt.app.application.playground.dto;

import java.util.List;

public record PlaygroundRecentPostsResponse(
	List<PlaygroundRecentPost> recentPosts
) {
	public static PlaygroundRecentPostsResponse from(List<PlaygroundRecentPost> recentPosts) {
		return new PlaygroundRecentPostsResponse(recentPosts);
	}
}
