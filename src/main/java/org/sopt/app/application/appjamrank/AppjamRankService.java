package org.sopt.app.application.appjamrank;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.sopt.app.domain.entity.AppjamUser;
import org.sopt.app.domain.entity.soptamp.SoptampUser;
import org.sopt.app.domain.entity.soptamp.Stamp;
import org.sopt.app.interfaces.postgres.AppjamUserRepository;
import org.sopt.app.interfaces.postgres.SoptampUserRepository;
import org.sopt.app.interfaces.postgres.StampRepository;
import org.sopt.app.interfaces.postgres.StampRepositoryCustom;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AppjamRankService {
	private final StampRepository stampRepository;
	private final AppjamUserRepository appjamUserRepository;
    private final SoptampUserRepository soptampUserRepository;

	public AppjamRankInfo.RankAggregate findRecentTeamRanks(Pageable pageable) {

		List<Stamp> latestStamps = stampRepository.findDisplayedLatestStamps(pageable);
		if (latestStamps.isEmpty()) {
			return AppjamRankInfo.RankAggregate.empty();
		}

		List<Long> uploaderUserIds = latestStamps.stream()
			.map(Stamp::getUserId)
			.distinct()
			.toList();

		List<AppjamUser> uploaderAppjamUsers = appjamUserRepository.findAllByUserIdIn(uploaderUserIds);
        List<SoptampUser> uploaderSoptampUsers = soptampUserRepository.findAllByUserIdIn(uploaderUserIds);

        Map<Long, AppjamUser> uploaderAppjamUserByUserId = uploaderAppjamUsers.stream()
			.collect(Collectors.toMap(
				AppjamUser::getUserId,
				Function.identity(),
				(existing, replacement) -> existing
			));

        Map<Long, SoptampUser> uploaderSoptampUserByUserId = uploaderSoptampUsers.stream()
            .collect(Collectors.toMap(
                SoptampUser::getUserId,
                Function.identity(),
                (existing, replacement) -> existing
            ));

		return AppjamRankInfo.RankAggregate.of(
			latestStamps,
			uploaderUserIds,
			uploaderAppjamUserByUserId,
            uploaderSoptampUserByUserId
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

	public Map<Long, Long> findTotalPointsByUserIds(Collection<Long> userIds) {
		if (userIds == null || userIds.isEmpty()) {
			return Map.of();
		}

		return soptampUserRepository.findAllByUserIdIn(userIds).stream()
			.collect(Collectors.toMap(
				SoptampUser::getUserId,
				user -> user.getTotalPoints() == null ? 0L : user.getTotalPoints(),
				(existing, replacement) -> existing
			));
	}

	public Optional<AppjamUser> findAppjamUserByUserId(Long userId) {
		return appjamUserRepository.findByUserId(userId);
	}
}
