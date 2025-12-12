package org.sopt.app.facade;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.sopt.app.application.appjamrank.AppjamRankInfo;
import org.sopt.app.application.appjamrank.AppjamRankCalculator;
import org.sopt.app.application.appjamrank.AppjamRankListInfo;
import org.sopt.app.application.appjamrank.AppjamRankService;
import org.sopt.app.application.playground.PlaygroundAuthService;
import org.sopt.app.application.playground.dto.PlaygroundProfileInfo;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AppjamRankFacade {

	private final PlaygroundAuthService playgroundAuthService;
	private final AppjamRankService appjamRankService;

	@Transactional(readOnly = true)
	public AppjamRankListInfo findRecentTeamRanks() {

		AppjamRankInfo baseResult = appjamRankService.findRecentTeamRanks();
		if (baseResult.getLatestStamps().isEmpty()) {
			return AppjamRankListInfo.of(List.of());
		}

		List<PlaygroundProfileInfo.PlaygroundProfile> playgroundProfiles =
			playgroundAuthService.getPlaygroundMemberProfiles(baseResult.getUploaderUserIds());

		Map<Long, PlaygroundProfileInfo.PlaygroundProfile> playgroundProfileByUserId = playgroundProfiles.stream()
			.collect(Collectors.toMap(
				PlaygroundProfileInfo.PlaygroundProfile::getMemberId,
				Function.identity(),
				(existing, replacement) -> existing,
				LinkedHashMap::new
			));

		AppjamRankCalculator appjamRankCalculator = new AppjamRankCalculator(
			baseResult.getLatestStamps(),
			baseResult.getUploaderAppjamUserByUserId(),
			baseResult.getTeamInfoByTeamNumber(),
			playgroundProfileByUserId
		);

		List<AppjamRankListInfo.TeamRankInfo> ranks = appjamRankCalculator.calculateRecentTeamRanks();
		return AppjamRankListInfo.of(ranks);
	}
}
