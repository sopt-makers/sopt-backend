package org.sopt.app.application.appjamrank;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.sopt.app.application.playground.dto.PlaygroundProfileInfo;
import org.sopt.app.common.exception.BadRequestException;
import org.sopt.app.common.response.ErrorCode;
import org.sopt.app.domain.entity.AppjamUser;
import org.sopt.app.domain.entity.soptamp.Stamp;
import org.sopt.app.domain.enums.TeamNumber;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor(access = AccessLevel.PUBLIC)
public class AppjamRankCalculator {

	private static final int TODAY_TEAM_RANK_LIMIT = 10;

	private final List<Stamp> latestStamps;
	private final Map<Long, AppjamUser> uploaderAppjamUserByUserId;
	private final Map<Long, PlaygroundProfileInfo.PlaygroundProfile> playgroundProfileByUserId;

	public List<AppjamRankInfo.TeamRank> calculateRecentTeamRanks() {
		return latestStamps.stream()
			.map(stamp -> {
				AppjamUser uploaderAppjamUser = Optional.ofNullable(uploaderAppjamUserByUserId.get(stamp.getUserId()))
					.orElseThrow(() -> new BadRequestException(ErrorCode.USER_NOT_FOUND));

				PlaygroundProfileInfo.PlaygroundProfile playgroundProfile =
					Optional.ofNullable(playgroundProfileByUserId.get(stamp.getUserId()))
						.orElseThrow(() -> new BadRequestException(ErrorCode.USER_NOT_FOUND));

				TeamNumber teamNumber = uploaderAppjamUser.getTeamNumber();

				String firstImageUrl = Optional.ofNullable(stamp.getImages())
					.filter(images -> !images.isEmpty())
					.map(List::getFirst)
					.orElse("");

				return AppjamRankInfo.TeamRank.of(
					stamp,
					firstImageUrl,
					uploaderAppjamUser,
					teamNumber,
					playgroundProfile
				);
			})
			.toList();
	}

	public AppjamRankInfo.TodayTeamRankList calculateTodayTeamRanksTop10(
		List<AppjamRankInfo.TodayRank> todayUserRanks,
		Map<Long, Long> totalPointsByUserId,
		List<AppjamUser> allAppjamUsers
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
			.collect(Collectors.groupingBy(
				AppjamUser::getTeamNumber
			));

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
			.limit(TODAY_TEAM_RANK_LIMIT)
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
