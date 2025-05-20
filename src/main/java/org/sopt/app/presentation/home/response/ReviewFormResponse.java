package org.sopt.app.presentation.home.response;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class ReviewFormResponse {
	private String title;
	private String subTitle;
	private String actionButtonName;
	private String linkUrl;
	private Boolean isActive;

	public static ReviewFormResponse of(String title, String subTitle,
		String actionButtonName, String linkUrl, Boolean isActive) {
		return ReviewFormResponse.builder()
			.title(title)
			.subTitle(subTitle)
			.actionButtonName(actionButtonName)
			.linkUrl(linkUrl)
			.isActive(isActive)
			.build();
	}
}
