package org.sopt.app.application.appjamrank;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.sopt.app.domain.entity.AppjamUser;
import org.sopt.app.domain.entity.soptamp.Stamp;
import org.sopt.app.domain.enums.TeamNumber;
import org.sopt.app.interfaces.postgres.AppjamUserRepository;
import org.sopt.app.interfaces.postgres.StampRepository;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AppjamRankService {

	private static final int RANK_LIMIT = 3;

	private final StampRepository stampRepository;
	private final AppjamUserRepository appjamUserRepository;

	@Transactional(readOnly = true)
	public List<AppjamRankListInfo.TeamRankInfo> getRecentTeamRankBaseInfos() {
		Pageable top3Pageable = PageRequest.of(0, RANK_LIMIT);

		List<TeamNumber> topTeamNumbers = stampRepository.findTopTeamNumbersByLatestStamp(top3Pageable);

		if (topTeamNumbers.isEmpty()) {
			return List.of();
		}

		Map<TeamNumber, Stamp> latestStampByTeamNumber = new LinkedHashMap<>();
		for (TeamNumber teamNumber : topTeamNumbers) {
			List<Stamp> stamps = stampRepository.findLatestStampByTeamNumber(
				teamNumber,
				PageRequest.of(0, 1)
			);
			stamps.stream().findFirst()
				.ifPresent(stamp -> latestStampByTeamNumber.put(teamNumber, stamp));
		}

		if (latestStampByTeamNumber.isEmpty()) {
			return List.of();
		}

		List<AppjamUser> teamInfos = appjamUserRepository.findAllByTeamNumberIn(topTeamNumbers);

		Map<TeamNumber, AppjamUser> teamInfoByTeamNumber = teamInfos.stream()
			.collect(Collectors.toMap(
				AppjamUser::getTeamNumber,
				Function.identity()
			));

		List<AppjamRankListInfo.TeamRankInfo> teamRankInfos = new ArrayList<>();

		for (TeamNumber teamNumber : topTeamNumbers) {
			Stamp stamp = latestStampByTeamNumber.get(teamNumber);
			AppjamUser appjamUser = teamInfoByTeamNumber.get(teamNumber);

			String firstImageUrl = Optional.ofNullable(stamp.getImages())
				.filter(images -> !images.isEmpty())
				.map(images -> images.getFirst())
				.orElse("");

			teamRankInfos.add(AppjamRankListInfo.TeamRankInfo.builder()
				.stampId(stamp.getId())
				.missionId(stamp.getMissionId())
				.userId(stamp.getUserId())
				.imageUrl(firstImageUrl)
				.createdAt(stamp.getCreatedAt())
				.teamName(appjamUser.getTeamName())
				.teamNumber(teamNumber)
				.build());
		}

		return teamRankInfos;
	}
}
