package org.sopt.app.facade;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.sopt.app.application.appjamrank.AppjamRankInfo;
import org.sopt.app.application.appjamrank.AppjamRankCalculator;
import org.sopt.app.application.appjamrank.AppjamRankService;
import org.sopt.app.application.playground.PlaygroundAuthService;
import org.sopt.app.application.playground.dto.PlaygroundProfileInfo;
import org.sopt.app.application.rank.RankCacheService;
import org.sopt.app.domain.entity.AppjamUser;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AppjamRankFacade {

	private final PlaygroundAuthService playgroundAuthService;
	private final AppjamRankService appjamRankService;
	private final RankCacheService rankCacheService;

	@Transactional(readOnly = true)
	public AppjamRankInfo.RankList findRecentTeamRanks() {

		AppjamRankInfo.RankAggregate rankAggregate = appjamRankService.findRecentTeamRanks();
		if (rankAggregate.getLatestStamps().isEmpty()) {
			return AppjamRankInfo.RankList.of(List.of());
		}

		List<PlaygroundProfileInfo.PlaygroundProfile> playgroundProfiles =
			playgroundAuthService.getPlaygroundMemberProfiles(rankAggregate.getUploaderUserIds());

		Map<Long, PlaygroundProfileInfo.PlaygroundProfile> playgroundProfileByUserId = playgroundProfiles.stream()
			.collect(Collectors.toMap(
				PlaygroundProfileInfo.PlaygroundProfile::getMemberId,
				Function.identity(),
				(existing, replacement) -> existing,
				LinkedHashMap::new
			));

		AppjamRankCalculator appjamRankCalculator = new AppjamRankCalculator(
			rankAggregate.getLatestStamps(),
			rankAggregate.getUploaderAppjamUserByUserId(),
			rankAggregate.getTeamInfoByTeamNumber(),
			playgroundProfileByUserId
		);

		List<AppjamRankInfo.TeamRank> ranks = appjamRankCalculator.calculateRecentTeamRanks();

		return AppjamRankInfo.RankList.of(ranks);
	}

	@Transactional(readOnly = true)
	public AppjamRankInfo.TodayTeamRankList findTodayTeamRanks() {

		LocalDateTime todayStart = LocalDate.now().atStartOfDay();
		LocalDateTime tomorrowStart = todayStart.plusDays(1);

		List<AppjamRankInfo.TodayRank> todayUserRanks = findTodayUserRanks(todayStart, tomorrowStart);
		Map<Long, Long> totalPointsByUserId = buildTotalPointsByUserId();
		List<AppjamUser> allAppjamUsers = appjamRankService.findAllAppjamUsers();

		AppjamRankCalculator appjamRankCalculator = new AppjamRankCalculator(
			List.of(),
			Map.of(),
			Map.of(),
			Map.of()
		);

		return appjamRankCalculator.calculateTodayTeamRanksTop10(
			todayUserRanks,
			totalPointsByUserId,
			allAppjamUsers
		);
	}

	private List<AppjamRankInfo.TodayRank> findTodayUserRanks(LocalDateTime todayStart, LocalDateTime tomorrowStart) {
		return appjamRankService.findTodayUserRankSources(todayStart, tomorrowStart).stream()
			.map(source -> AppjamRankInfo.TodayRank.of(
				source.userId(),
				source.todayPoints(),
				0L,
				source.firstCertifiedAtToday()
			))
			.toList();
	}

	private Map<Long, Long> buildTotalPointsByUserId() {
		Set<ZSetOperations.TypedTuple<Long>> ranking = rankCacheService.getRanking();
		if (ranking == null || ranking.isEmpty()) {
			return Map.of();
		}

		return ranking.stream()
			.filter(tuple -> tuple.getValue() != null)
			.collect(Collectors.toMap(
				ZSetOperations.TypedTuple::getValue,
				tuple -> tuple.getScore() == null ? 0L : tuple.getScore().longValue(),
				(existing, replacement) -> existing,
				LinkedHashMap::new
			));
	}
}
