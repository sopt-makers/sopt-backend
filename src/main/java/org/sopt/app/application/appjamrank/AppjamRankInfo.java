package org.sopt.app.application.appjamrank;

import java.util.List;
import java.util.Map;

import org.sopt.app.domain.entity.AppjamUser;
import org.sopt.app.domain.entity.soptamp.Stamp;
import org.sopt.app.domain.enums.TeamNumber;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class AppjamRankInfo {

	private final List<Stamp> latestStamps;
	private final List<Long> uploaderUserIds;
	private final Map<Long, AppjamUser> uploaderAppjamUserByUserId;
	private final Map<TeamNumber, AppjamUser> teamInfoByTeamNumber;

	public static AppjamRankInfo of(
		List<Stamp> latestStamps,
		List<Long> uploaderUserIds,
		Map<Long, AppjamUser> uploaderAppjamUserByUserId,
		Map<TeamNumber, AppjamUser> teamInfoByTeamNumber
	) {
		return AppjamRankInfo.builder()
			.latestStamps(latestStamps)
			.uploaderUserIds(uploaderUserIds)
			.uploaderAppjamUserByUserId(uploaderAppjamUserByUserId)
			.teamInfoByTeamNumber(teamInfoByTeamNumber)
			.build();
	}

	public static AppjamRankInfo empty() {
		return AppjamRankInfo.builder()
			.latestStamps(List.of())
			.uploaderUserIds(List.of())
			.uploaderAppjamUserByUserId(Map.of())
			.teamInfoByTeamNumber(Map.of())
			.build();
	}
}
