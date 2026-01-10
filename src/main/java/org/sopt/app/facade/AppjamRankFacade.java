package org.sopt.app.facade;

import java.time.LocalDate;
import java.time.LocalDateTime;
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
import org.sopt.app.interfaces.postgres.StampRepositoryCustom;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
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
	public AppjamRankInfo.RankList findRecentTeamRanks(int size) {
		Pageable pageable = PageRequest.of(0, size);

		AppjamRankInfo.RankAggregate aggregate = appjamRankService.findRecentTeamRanks(pageable);
		if (aggregate.getLatestStamps().isEmpty()) {
			return AppjamRankInfo.RankList.of(List.of());
		}

		List<PlaygroundProfileInfo.PlaygroundProfile> playgroundProfiles = playgroundAuthService.getPlaygroundMemberProfiles(aggregate.getUploaderUserIds());

		Map<Long, PlaygroundProfileInfo.PlaygroundProfile> playgroundProfileByUserId = playgroundProfiles.stream()
			.collect(Collectors.toMap(
				PlaygroundProfileInfo.PlaygroundProfile::getMemberId,
				Function.identity(),
				(existing, replacement) -> existing
			));

		AppjamRankCalculator calculator = new AppjamRankCalculator(
			aggregate.getLatestStamps(),
			aggregate.getUploaderAppjamUserByUserId(),
			playgroundProfileByUserId
		);

		List<AppjamRankInfo.TeamRank> ranks = calculator.calculateRecentTeamRanks(size);
		return AppjamRankInfo.RankList.of(ranks);
	}

	@Transactional(readOnly = true)
	public AppjamRankInfo.TodayTeamRankList findTodayTeamRanks(int size) {

		LocalDateTime todayStart = LocalDate.now().atStartOfDay();
		LocalDateTime tomorrowStart = todayStart.plusDays(1);
		List<AppjamRankInfo.TodayRank> todayUserRanks = findTodayUserRanks(todayStart, tomorrowStart);
		Map<Long, Long> totalPointsByUserId = buildTotalPointsByUserId();
		List<AppjamUser> allAppjamUsers = appjamRankService.findAllAppjamUsers();

		AppjamRankCalculator calculator = new AppjamRankCalculator(
			List.of(),
			Map.of(),
			Map.of()
		);

		return calculator.calculateTodayTeamRanks(
			todayUserRanks,
			totalPointsByUserId,
			allAppjamUsers,
			size
		);
	}

	private List<AppjamRankInfo.TodayRank> findTodayUserRanks(LocalDateTime todayStart, LocalDateTime tomorrowStart) {
		List<StampRepositoryCustom.AppjamTodayRankSource> sources =
			appjamRankService.findTodayUserRankSources(todayStart, tomorrowStart);

		return sources.stream()
			.map(source -> AppjamRankInfo.TodayRank.of(
				source.userId(),
				source.todayPoints(),
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
				(existing, replacement) -> existing
			));
	}
}
