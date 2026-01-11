package org.sopt.app.application.appjamrank;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Function;
import java.util.stream.Collectors;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import org.sopt.app.application.playground.dto.PlaygroundProfileInfo;
import org.sopt.app.domain.entity.AppjamUser;
import org.sopt.app.domain.entity.soptamp.SoptampUser;
import org.sopt.app.domain.entity.soptamp.Stamp;
import org.sopt.app.domain.enums.TeamNumber;

@RequiredArgsConstructor(access = AccessLevel.PUBLIC)
public class AppjamRankCalculator {

	private final List<Stamp> latestStamps;
	private final Map<Long, AppjamUser> uploaderAppjamUserByUserId;
    private final Map<Long, SoptampUser> uploaderSoptampUserByUserId;
	private final Map<Long, PlaygroundProfileInfo.PlaygroundProfile> playgroundProfileByUserId;

	public List<AppjamRankInfo.TeamRank> calculateRecentTeamRanks(int size) {
		return latestStamps.stream()
			.map(stamp -> {
				AppjamUser uploaderAppjamUser = uploaderAppjamUserByUserId.get(stamp.getUserId());
                SoptampUser uploaderSoptampUser = uploaderSoptampUserByUserId.get(stamp.getUserId());
				if (uploaderAppjamUser == null || uploaderSoptampUser == null) {
					return null;
				}

				PlaygroundProfileInfo.PlaygroundProfile playgroundProfile =
					playgroundProfileByUserId.get(stamp.getUserId());
				if (playgroundProfile == null) {
					return null;
				}

				String firstImageUrl = Optional.ofNullable(stamp.getImages())
					.filter(images -> !images.isEmpty())
					.map(List::getFirst)
					.orElse("");

				return AppjamRankInfo.TeamRank.of(
					stamp,
					firstImageUrl,
                    uploaderSoptampUser,
					uploaderAppjamUser,
					uploaderAppjamUser.getTeamNumber(),
					playgroundProfile
				);
			})
			.filter(Objects::nonNull)
			.limit(size)
			.toList();
	}

	public AppjamRankInfo.TodayTeamRankList calculateTodayTeamRanks(
		List<AppjamRankInfo.TodayRank> todayUserRanks,
		Map<Long, Long> totalPointsByUserId,
		List<AppjamUser> allAppjamUsers,
		int size
	) {
		Map<Long, AppjamRankInfo.TodayRank> todayRankByUserId = todayUserRanks.stream()
			.collect(Collectors.toMap(
				AppjamRankInfo.TodayRank::getUserId,
				Function.identity(),
				(existing, replacement) -> existing
			));

		Map<TeamNumber, String> teamNameByTeamNumber = allAppjamUsers.stream()
			.collect(Collectors.toMap(
				AppjamUser::getTeamNumber,
				AppjamUser::getTeamName,
				(existing, replacement) -> existing
			));

		Map<TeamNumber, List<AppjamUser>> membersByTeamNumber = allAppjamUsers.stream()
			.collect(Collectors.groupingBy(AppjamUser::getTeamNumber));

		List<TeamAggregate> teamAggregates = membersByTeamNumber.entrySet().stream()
			.map(entry -> aggregateTeam(
				entry.getKey(),
				entry.getValue(),
				teamNameByTeamNumber.getOrDefault(entry.getKey(), ""),
				todayRankByUserId,
				totalPointsByUserId
			))
			.sorted(Comparator
				.comparingLong(TeamAggregate::todayPoints).reversed()
				.thenComparing(TeamAggregate::firstCertifiedAtToday, Comparator.nullsLast(Comparator.naturalOrder()))
				.thenComparing(TeamAggregate::teamNumber)
			)
			.limit(size)
			.toList();

		AtomicInteger rankCounter = new AtomicInteger(1);
		List<AppjamRankInfo.TodayTeamRank> ranks = teamAggregates.stream()
			.map(teamAggregate -> AppjamRankInfo.TodayTeamRank.of(
				rankCounter.getAndIncrement(),
				teamAggregate.teamNumber(),
				teamAggregate.teamName(),
				teamAggregate.todayPoints(),
				teamAggregate.totalPoints()
			))
			.toList();

		return AppjamRankInfo.TodayTeamRankList.of(ranks);
	}


	private TeamAggregate aggregateTeam(
		TeamNumber teamNumber,
		List<AppjamUser> teamMembers,
		String teamName,
		Map<Long, AppjamRankInfo.TodayRank> todayRankByUserId,
		Map<Long, Long> totalPointsByUserId
	) {
		long todayPointsSum = 0L;
		long totalPointsSum = 0L;
		LocalDateTime firstCertifiedAtToday = null;

		for (AppjamUser teamMember : teamMembers) {
			Long userId = teamMember.getUserId();

			AppjamRankInfo.TodayRank todayRank = todayRankByUserId.get(userId);
			if (todayRank != null) {
				todayPointsSum += todayRank.getTodayPoints();

				LocalDateTime certifiedAt = todayRank.getFirstCertifiedAtToday();
				if (certifiedAt != null && (firstCertifiedAtToday == null || certifiedAt.isBefore(firstCertifiedAtToday))) {
					firstCertifiedAtToday = certifiedAt;
				}
			}

			totalPointsSum += totalPointsByUserId.getOrDefault(userId, 0L);
		}

		return new TeamAggregate(teamNumber, teamName, todayPointsSum, totalPointsSum, firstCertifiedAtToday);
	}

	private record TeamAggregate(
		TeamNumber teamNumber,
		String teamName,
		long todayPoints,
		long totalPoints,
		LocalDateTime firstCertifiedAtToday
	) {}
}
