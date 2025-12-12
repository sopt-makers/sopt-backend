package org.sopt.app.application.appjamrank;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.sopt.app.application.playground.dto.PlaygroundProfileInfo;
import org.sopt.app.domain.entity.AppjamUser;
import org.sopt.app.domain.entity.soptamp.Stamp;
import org.sopt.app.domain.enums.TeamNumber;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor(access = AccessLevel.PUBLIC)
public class AppjamRankCalculator {

	private final List<Stamp> latestStamps;
	private final Map<Long, AppjamUser> uploaderAppjamUserByUserId;
	private final Map<TeamNumber, AppjamUser> teamInfoByTeamNumber;
	private final Map<Long, PlaygroundProfileInfo.PlaygroundProfile> playgroundProfileByUserId;

	public List<AppjamRankListInfo.TeamRankInfo> calculateRecentTeamRanks() {
		return latestStamps.stream()
			.map(stamp -> {
				AppjamUser uploaderAppjamUser = uploaderAppjamUserByUserId.get(stamp.getUserId());
				TeamNumber teamNumber = uploaderAppjamUser.getTeamNumber();

				AppjamUser teamInfo = teamInfoByTeamNumber.get(teamNumber);

				String firstImageUrl = Optional.ofNullable(stamp.getImages())
					.filter(images -> !images.isEmpty())
					.map(List::getFirst)
					.orElse("");

				PlaygroundProfileInfo.PlaygroundProfile playgroundProfile = playgroundProfileByUserId.get(stamp.getUserId());

				return AppjamRankListInfo.TeamRankInfo.of(
					stamp,
					firstImageUrl,
					teamInfo,
					teamNumber,
					playgroundProfile
				);
			})
			.toList();
	}
}
