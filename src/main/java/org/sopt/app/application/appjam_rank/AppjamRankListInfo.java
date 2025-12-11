package org.sopt.app.application.appjam_rank;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

import org.sopt.app.domain.enums.TeamNumber;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class AppjamRankListInfo {

	private final List<AppjamtampRankInfo> ranks;

	public static AppjamRankListInfo of(List<AppjamtampRankInfo> ranks) {
		return AppjamRankListInfo.builder()
			.ranks(ranks)
			.build();
	}

	public static AppjamRankListInfo empty() {
		return AppjamRankListInfo.builder()
			.ranks(Collections.emptyList())
			.build();
	}

	@Getter
	@Builder
	public static class TeamRankInfo {

		private final Long stampId;
		private final Long missionId;
		private final Long userId;
		private final String imageUrl;
		private final LocalDateTime createdAt;

		private final String teamName;
		private final TeamNumber teamNumber;

		public AppjamtampRankInfo toAppjamtampRankInfo(
			String userName,
			String userProfileImage
		) {
			return AppjamtampRankInfo.builder()
				.stampId(stampId)
				.missionId(missionId)
				.userId(userId)
				.imageUrl(imageUrl)
				.createdAt(createdAt)
				.userName(userName)
				.userProfileImage(userProfileImage)
				.teamName(teamName)
				.teamNumber(teamNumber)
				.build();
		}
	}

	@Getter
	@Builder
	public static class AppjamtampRankInfo {

		private final Long stampId;
		private final Long missionId;
		private final Long userId;
		private final String imageUrl;
		private final LocalDateTime createdAt;

		private final String userName;
		private final String userProfileImage;

		private final String teamName;
		private final TeamNumber teamNumber;
	}
}
