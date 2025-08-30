package org.sopt.app.application.playground.dto;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.annotation.Nullable;

import org.sopt.app.common.config.OperationConfig;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

public record PlaygroundRecentPost(
	@JsonProperty("id") Long playgroundPostId,
	@Nullable Long userId,
	String profileImage,
	String name,
	String generationAndPart,
	String category,
	String title,
	String content,
	String webLink,
	@JsonIgnore String createdAt,
	@JsonProperty(value = "isOutdated", defaultValue = "false")
	boolean isOutdated
){
	private static final Map<String, String> CATEGORY_MESSAGES = Map.of(
		"자유", "에 오늘의 TMI 적어봐!",
		"파트Talk", "에 파트 자랑해봐!",
		"질문", "에 진로 고민 공유해봐!",
		"홍보", "에 팀 서비스 소개해봐!",
		"솝티클", "에 직무 인사이트 써봐!"
	);

	public static PlaygroundRecentPost from(
		Long postId, Long userId, String profileImage, String name, String generationAndPart,
		String category, String title, String content, String webLink, String createdAt,
		Map<String, String> imageConfigMap
	) {
		boolean isOutdated = isOutdated(createdAt);

		if (isOutdated) {
			String defaultMessage = CATEGORY_MESSAGES.getOrDefault(category, "플레이그라운드에 새 글 올려봐!");
			String image = imageConfigMap.getOrDefault(category + ".imageUrl", imageConfigMap.get("unknown.imageUrl"));
			return new PlaygroundRecentPost(
				null,
				null,
				image,
				null,
				null,
				category,
				"아직 최신글이 없어요",
				defaultMessage,
				trimWebLink(webLink),
				createdAt,
				true
			);
		}

		return new PlaygroundRecentPost(
			postId, userId, profileImage, name, generationAndPart, category, title, content, webLink, createdAt, false);
	}

	private static boolean isOutdated(String createdAt) {
		try {
			LocalDateTime created = LocalDateTime.parse(createdAt, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSSSSS"));
			return ChronoUnit.DAYS.between(created, LocalDateTime.now()) > 30;
		} catch (Exception e) {
			return false;
		}
	}

	private static String trimWebLink(String webLink) {
		if (webLink == null) return null;
		return webLink.split("\\?")[0];
	}

	public static Map<String, String> toImageConfigMap(List<OperationConfig> configs) {
		return configs.stream()
			.collect(Collectors.toMap(OperationConfig::getKey, OperationConfig::getValue));
	}
}
