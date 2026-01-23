package org.sopt.app.facade;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.sopt.app.application.appjamrank.AppjamRankCalculator;
import org.sopt.app.application.appjamrank.AppjamRankInfo;
import org.sopt.app.application.appjamrank.AppjamRankService;
import org.sopt.app.application.playground.PlaygroundAuthService;
import org.sopt.app.application.playground.dto.PlaygroundProfileInfo;
import org.sopt.app.common.utils.CurrentDate;
import org.sopt.app.domain.entity.AppjamUser;
import org.sopt.app.domain.enums.TeamNumber;
import org.sopt.app.interfaces.postgres.StampRepositoryCustom;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AppjamRankFacade {

	private final PlaygroundAuthService playgroundAuthService;
	private final AppjamRankService appjamRankService;

	@Transactional(readOnly = true)
	public AppjamRankInfo.RankList findRecentTeamRanks(int size) {
		Pageable pageable = PageRequest.of(0, size);

		AppjamRankInfo.RankAggregate aggregate = appjamRankService.findRecentTeamRanks(pageable);
		if (aggregate.getLatestStamps().isEmpty()) {
			return AppjamRankInfo.RankList.of(List.of());
		}

		List<PlaygroundProfileInfo.PlaygroundProfile> playgroundProfiles =
			playgroundAuthService.getPlaygroundMemberProfiles(aggregate.getUploaderUserIds());

		Map<Long, PlaygroundProfileInfo.PlaygroundProfile> playgroundProfileByUserId = playgroundProfiles.stream()
			.collect(Collectors.toMap(
				PlaygroundProfileInfo.PlaygroundProfile::getMemberId,
				Function.identity(),
				(existing, replacement) -> existing
			));

		AppjamRankCalculator calculator = new AppjamRankCalculator(
			aggregate.getLatestStamps(),
			aggregate.getUploaderAppjamUserByUserId(),
			aggregate.getUploaderSoptampUserByUserId(),
			playgroundProfileByUserId
		);

		return AppjamRankInfo.RankList.of(calculator.calculateRecentTeamRanks(size));
	}

	/**
	 * 오늘 팀 랭킹 (캐시 없이 DB 기반)
	 * - 전체 팀을 항상 보여주기 위해 effectiveSize는 teamCount 이상 보장
	 */
	@Transactional(readOnly = true)
	public AppjamRankInfo.TodayTeamRankList findTodayTeamRanks(int size) {
		LocalDateTime todayStart = CurrentDate.now().atStartOfDay();
		LocalDateTime tomorrowStart = todayStart.plusDays(1);

		List<AppjamRankInfo.TodayRank> todayUserRanks = findTodayUserRanks(todayStart, tomorrowStart);
		List<AppjamUser> allAppjamUsers = appjamRankService.findAllAppjamUsers().stream()
			.filter(u -> u.getTeamNumber() != null)
			.toList();

		List<Long> appjamUserIds = allAppjamUsers.stream()
			.map(AppjamUser::getUserId)
			.distinct()
			.toList();

		Map<Long, Long> totalPointsByUserId = appjamRankService.findTotalPointsByUserIds(appjamUserIds);

		int teamCount = (int) allAppjamUsers.stream()
			.map(AppjamUser::getTeamNumber)
			.distinct()
			.count();

		int effectiveSize = Math.max(Math.max(size, 1), teamCount);

		AppjamRankCalculator calculator = new AppjamRankCalculator(
			List.of(),
			Map.of(),
			Map.of(),
			Map.of()
		);

		return calculator.calculateTodayTeamRanks(
			todayUserRanks,
			totalPointsByUserId,
			allAppjamUsers,
			effectiveSize
		);
	}

	@Transactional(readOnly = true)
	public Integer findMyTeamRank(final Long userId) {
		AppjamUser myAppjamUser = appjamRankService.findAppjamUserByUserId(userId).orElse(null);
		if (myAppjamUser == null || myAppjamUser.getTeamNumber() == null) {
			return null;
		}

		TeamNumber myTeamNumber = myAppjamUser.getTeamNumber();
		AppjamRankInfo.TodayTeamRankList ranks = findTodayTeamRanks(0);

		return ranks.getRanks().stream()
			.filter(r -> r.getTeamNumber() == myTeamNumber)
			.map(AppjamRankInfo.TodayTeamRank::getRank)
			.findFirst()
			.orElse(null);
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
}
