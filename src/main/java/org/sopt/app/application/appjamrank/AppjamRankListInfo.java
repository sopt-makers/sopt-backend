package org.sopt.app.application.appjamrank;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.sopt.app.application.playground.dto.PlaygroundProfileInfo;
import org.sopt.app.domain.entity.AppjamUser;
import org.sopt.app.domain.entity.soptamp.Stamp;
import org.sopt.app.domain.enums.TeamNumber;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class AppjamRankListInfo {

	private final List<TeamRankInfo> ranks;

	public static AppjamRankListInfo of(List<TeamRankInfo> ranks) {
		return AppjamRankListInfo.builder()
			.ranks(ranks)
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
		private final String userName;
		private final String userProfileImage;
		private final String teamName;
		private final TeamNumber teamNumber;

		public static TeamRankInfo of(
			Stamp stamp,
			String firstImageUrl,
			AppjamUser teamInfo,
			TeamNumber teamNumber,
			PlaygroundProfileInfo.PlaygroundProfile playgroundProfile
		) {
			return TeamRankInfo.builder()
				.stampId(stamp.getId())
				.missionId(stamp.getMissionId())
				.userId(stamp.getUserId())
				.imageUrl(firstImageUrl)
				.createdAt(stamp.getCreatedAt())
				.userName(playgroundProfile.getName())
				.userProfileImage(Optional.ofNullable(playgroundProfile.getProfileImage()).orElse(""))
				.teamName(teamInfo.getTeamName())
				.teamNumber(teamNumber)
				.build();
		}
	}
}
