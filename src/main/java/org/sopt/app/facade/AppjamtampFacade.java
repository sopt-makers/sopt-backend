package org.sopt.app.facade;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

import lombok.RequiredArgsConstructor;
import lombok.val;

import org.sopt.app.application.appjamrank.AppjamRankListInfo;
import org.sopt.app.application.appjamrank.AppjamRankService;
import org.sopt.app.application.appjamuser.AppjamUserService;
import org.sopt.app.application.platform.PlatformService;
import org.sopt.app.application.platform.dto.PlatformUserInfoResponse;
import org.sopt.app.application.soptamp.SoptampUserFinder;
import org.sopt.app.application.stamp.ClapService;
import org.sopt.app.application.stamp.StampInfo;
import org.sopt.app.application.stamp.StampInfo.AppjamtampView;
import org.sopt.app.application.stamp.StampService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AppjamtampFacade {

    private final SoptampUserFinder soptampUserFinder;
    private final PlatformService platformService;
    private final StampService stampService;
    private final ClapService clapService;
    private final AppjamUserService appjamUserService;
    private final AppjamRankService appjamRankService;

    public AppjamtampView getAppjamtamps(Long requestUserId, Long missionId, String nickname) {
        val owner = soptampUserFinder.findByNickname(nickname);
        val ownerUserId = owner.getUserId();
        val platformUserInfoResponse = platformService.getPlatformUserInfoResponse(ownerUserId);
        val teamSummary = appjamUserService.getTeamSummaryByUserId(ownerUserId);
        val stamp = stampService.findStamp(missionId, ownerUserId);
        val requestUserClapCount = clapService.getUserClapCount(requestUserId, stamp.getId());
        stampService.increaseViewCountById(stamp.getId());

        return StampInfo.AppjamtampView.of(
            stamp,
            requestUserClapCount,
            Objects.equals(requestUserId, ownerUserId),
            owner.getNickname(),
            platformUserInfoResponse.profileImage(),
            teamSummary
        );
    }

    @Transactional(readOnly = true)
    public AppjamRankListInfo getRecentTeamRanks() {
        List<AppjamRankListInfo.TeamRankInfo> baseInfos = appjamRankService.getRecentTeamRankBaseInfos();

        List<Long> userIds = baseInfos.stream()
            .map(AppjamRankListInfo.TeamRankInfo::getUserId)
            .distinct()
            .toList();

        List<PlatformUserInfoResponse> platformUserInfoResponses = platformService.getPlatformUserInfosResponse(userIds);
        Map<Long, PlatformUserInfoResponse> platformUserInfoByUserId =
            platformUserInfoResponses.stream()
                .collect(Collectors.toMap(
                    response -> (long) response.userId(),
                    Function.identity()
                ));

        List<AppjamRankListInfo.AppjamtampRankInfo> rankInfos = baseInfos.stream()
            .map(baseInfo -> {
                PlatformUserInfoResponse platformUserInfoResponse = platformUserInfoByUserId.get(baseInfo.getUserId());
                String userName = platformUserInfoResponse.name();
                String userProfileImage = Optional.ofNullable(platformUserInfoResponse.profileImage()).orElse("");

                return baseInfo.toAppjamtampRankInfo(userName, userProfileImage);
            })
            .toList();

        return AppjamRankListInfo.of(rankInfos);
    }
}
