package org.sopt.app.interfaces.postgres;

import java.time.LocalDateTime;
import java.util.List;

public interface StampRepositoryCustom {

	/**
	 * stamp.clap_count를 increment만큼 원자적으로 증가시키고
	 * 증가된 clap_count와 version을 함께 반환한다.
	 */
	StampCounts incrementClapCountReturning(Long stampId, int increment);

	record StampCounts(int clapCount, long version) {}

	List<AppjamTodayRankSource> findTodayUserRankSources(LocalDateTime todayStart, LocalDateTime tomorrowStart);

	record AppjamTodayRankSource(
		Long userId,
		long todayPoints,
		LocalDateTime firstCertifiedAtToday
	) {}
}
