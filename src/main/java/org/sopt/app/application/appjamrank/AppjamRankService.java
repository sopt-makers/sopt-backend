package org.sopt.app.application.appjamrank;

import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.sopt.app.domain.entity.AppjamUser;
import org.sopt.app.domain.entity.soptamp.Stamp;
import org.sopt.app.domain.enums.TeamNumber;
import org.sopt.app.interfaces.postgres.AppjamUserRepository;
import org.sopt.app.interfaces.postgres.StampRepository;
import org.sopt.app.interfaces.postgres.StampRepositoryCustom;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AppjamRankService {

	private static final int RECENT_RANK_LIMIT = 3;

	private final StampRepository stampRepository;
	private final AppjamUserRepository appjamUserRepository;

	public AppjamRankInfo.RankAggregate findRecentTeamRanks() {

		Pageable latestStampPageable = PageRequest.of(0, RECENT_RANK_LIMIT);

		List<Stamp> latestStamps = stampRepository.findLatestStamps(latestStampPageable);
		if (latestStamps.isEmpty()) {
			return AppjamRankInfo.RankAggregate.empty();
		}

		List<Long> uploaderUserIds = latestStamps.stream()
			.map(Stamp::getUserId)
			.distinct()
			.toList();

		List<AppjamUser> uploaderAppjamUsers = appjamUserRepository.findAllByUserIdIn(uploaderUserIds);

		Map<Long, AppjamUser> uploaderAppjamUserByUserId = uploaderAppjamUsers.stream()
			.collect(Collectors.toMap(
				AppjamUser::getUserId,
				Function.identity(),
				(existing, replacement) -> existing,
				LinkedHashMap::new
			));

		List<TeamNumber> teamNumbers = uploaderAppjamUsers.stream()
			.map(AppjamUser::getTeamNumber)
			.distinct()
			.toList();

		List<AppjamUser> teamUsers = appjamUserRepository.findAllByTeamNumberIn(teamNumbers);

		Map<TeamNumber, AppjamUser> teamInfoByTeamNumber = teamUsers.stream()
			.collect(Collectors.toMap(
				AppjamUser::getTeamNumber,
				Function.identity(),
				(existing, replacement) -> existing,
				LinkedHashMap::new
			));

		return AppjamRankInfo.RankAggregate.of(
			latestStamps,
			uploaderUserIds,
			uploaderAppjamUserByUserId,
			teamInfoByTeamNumber
		);
	}

	public List<StampRepositoryCustom.AppjamTodayRankSource> findTodayUserRankSources(
		LocalDateTime todayStart,
		LocalDateTime tomorrowStart
	) {
		return stampRepository.findTodayUserRankSources(todayStart, tomorrowStart);
	}

	public List<AppjamUser> findAllAppjamUsers() {
		return appjamUserRepository.findAll();
	}
}
