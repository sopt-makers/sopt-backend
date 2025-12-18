package org.sopt.app.presentation.appjamtamp;

import java.util.List;

import org.mapstruct.InjectionStrategy;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;
import org.sopt.app.application.appjamrank.AppjamRankInfo;
import org.sopt.app.application.mission.MissionInfo.AppjamMissionInfo;
import org.sopt.app.application.mission.MissionInfo.AppjamMissionInfos;
import org.sopt.app.application.stamp.StampInfo.AppjamtampView;
import org.sopt.app.presentation.appjamrank.AppjamRankResponse;
import org.sopt.app.presentation.appjamtamp.AppjamtampResponse.AppjamMissionResponse;
import org.sopt.app.presentation.appjamtamp.AppjamtampResponse.AppjamMissionResponses;

@Mapper(
    componentModel = "spring",
    injectionStrategy = InjectionStrategy.CONSTRUCTOR,
    unmappedTargetPolicy = ReportingPolicy.ERROR
)
public interface AppjamtampResponseMapper {

    AppjamMissionResponses of(AppjamMissionInfos missionList);

    AppjamRankResponse.AppjamtampRankResponse toResponse(AppjamRankInfo.TeamRank teamRank);

    // TeamMissionInfo to TeamMissionResponse
    @Mapping(source = "completed", target = "isCompleted")
    AppjamMissionResponse toResponse(AppjamMissionInfo info);

    default AppjamRankResponse.AppjamtampRankListResponse of(AppjamRankInfo.RankList appjamRankList) {
        List<AppjamRankResponse.AppjamtampRankResponse> ranks = appjamRankList.getRanks().stream()
            .map(this::toResponse)
            .toList();

        return new AppjamRankResponse.AppjamtampRankListResponse(ranks);
    }

    default AppjamRankResponse.AppjamTodayRankListResponse of(AppjamRankInfo.TodayTeamRankList todayTeamRankList) {
        List<AppjamRankResponse.AppjamTodayTeamRankResponse> ranks = todayTeamRankList.getRanks().stream()
            .map(teamRank -> new AppjamRankResponse.AppjamTodayTeamRankResponse(
                teamRank.getRank(),
                teamRank.getTeamName(),
                teamRank.getTodayPoints(),
                teamRank.getTotalPoints()
            ))
            .toList();

        return new AppjamRankResponse.AppjamTodayRankListResponse(ranks);
    }

    default AppjamtampResponse.AppjamtampView of(AppjamtampView appjamtampView) {
        return AppjamtampResponse.AppjamtampView.builder()
            .id(appjamtampView.getId())
            .contents(appjamtampView.getContents())
            .images(appjamtampView.getImages())
            .activityDate(appjamtampView.getActivityDate())
            .createdAt(appjamtampView.getCreatedAt())
            .updatedAt(appjamtampView.getUpdatedAt())
            .missionId(appjamtampView.getMissionId())
            .teamNumber(appjamtampView.getTeamNumber())
            .teamName(appjamtampView.getTeamName())
            .ownerNickname(appjamtampView.getOwnerNickName())
            .ownerProfileImage(appjamtampView.getOwnerProfileImage())
            .clapCount(appjamtampView.getClapCount())
            .viewCount(appjamtampView.getViewCount())
            .myClapCount(appjamtampView.getMyClapCount())
            .isMine(appjamtampView.isMine())
            .build();
    }
}
