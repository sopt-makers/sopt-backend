package org.sopt.app.application.appjamrank;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.sopt.app.application.playground.dto.PlaygroundProfileInfo;
import org.sopt.app.domain.entity.AppjamUser;
import org.sopt.app.domain.entity.soptamp.SoptampUser;
import org.sopt.app.domain.entity.soptamp.Stamp;
import org.sopt.app.domain.enums.TeamNumber;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class AppjamRankInfo {

	@Getter
	@Builder
	public static class RankAggregate {

		private final List<Stamp> latestStamps;
		private final List<Long> uploaderUserIds;
		private final Map<Long, AppjamUser> uploaderAppjamUserByUserId;
        private final Map<Long, SoptampUser> uploaderSoptampUserByUserId;

		public static RankAggregate of(
			List<Stamp> latestStamps,
			List<Long> uploaderUserIds,
			Map<Long, AppjamUser> uploaderAppjamUserByUserId,
            Map<Long, SoptampUser> uploaderSoptampUserByUserId
		) {
			return RankAggregate.builder()
				.latestStamps(latestStamps)
				.uploaderUserIds(uploaderUserIds)
				.uploaderAppjamUserByUserId(uploaderAppjamUserByUserId)
                .uploaderSoptampUserByUserId(uploaderSoptampUserByUserId)
				.build();
		}

		public static RankAggregate empty() {
			return RankAggregate.builder()
				.latestStamps(List.of())
				.uploaderUserIds(List.of())
				.uploaderAppjamUserByUserId(Map.of())
				.build();
		}
	}

	@Getter
	@Builder
	public static class TeamRank {
		private final Long stampId;
		private final Long missionId;
		private final Long userId;
		private final String imageUrl;
		private final LocalDateTime createdAt;
        private final String ownerNickname;
		private final String userName;
		private final String userProfileImage;
		private final String teamName;
		private final TeamNumber teamNumber;

		public static TeamRank of(
			Stamp stamp,
			String firstImageUrl,
            SoptampUser soptampUser,
			AppjamUser uploaderAppjamUser,
			TeamNumber teamNumber,
			PlaygroundProfileInfo.PlaygroundProfile playgroundProfile
		) {
			return TeamRank.builder()
				.stampId(stamp.getId())
				.missionId(stamp.getMissionId())
				.userId(stamp.getUserId())
				.imageUrl(firstImageUrl)
				.createdAt(stamp.getCreatedAt())
                .ownerNickname(soptampUser.getNickname())
				.userName(playgroundProfile.getName())
				.userProfileImage(Optional.ofNullable(playgroundProfile.getProfileImage()).orElse(""))
				.teamName(uploaderAppjamUser.getTeamName())
				.teamNumber(teamNumber)
				.build();
		}
	}

	@Getter
	@Builder
	@ToString
	public static class TodayRank {
		private final Long userId;
		private final long todayPoints;
		private final LocalDateTime firstCertifiedAtToday;

		public static TodayRank of(Long userId, long todayPoints, LocalDateTime firstCertifiedAtToday) {
			return TodayRank.builder()
				.userId(userId)
				.todayPoints(todayPoints)
				.firstCertifiedAtToday(firstCertifiedAtToday)
				.build();
		}
	}

	@Getter
	@Builder
	public static class RankList {
		private final List<TeamRank> ranks;

		public static RankList of(List<TeamRank> ranks) {
			return RankList.builder().ranks(ranks).build();
		}
	}

	@Getter
	@Builder
	public static class TodayTeamRank {
		private final int rank;
		private final TeamNumber teamNumber;
		private final String teamName;
		private final long todayPoints;
		private final long totalPoints;

		public static TodayTeamRank of(
			int rank,
			TeamNumber teamNumber,
			String teamName,
			long todayPoints,
			long totalPoints
		) {
			return TodayTeamRank.builder()
				.rank(rank)
				.teamNumber(teamNumber)
				.teamName(teamName)
				.todayPoints(todayPoints)
				.totalPoints(totalPoints)
				.build();
		}
	}

	@Getter
	@Builder
	public static class TodayTeamRankList {
		private final List<TodayTeamRank> ranks;

		public static TodayTeamRankList of(List<TodayTeamRank> ranks) {
			return TodayTeamRankList.builder().ranks(ranks).build();
		}
	}
}
