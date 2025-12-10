package org.sopt.app.presentation.appjamtamp;

import org.mapstruct.InjectionStrategy;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;
import org.sopt.app.application.mission.MissionInfo;

@Mapper(
    componentModel = "spring",
    injectionStrategy = InjectionStrategy.CONSTRUCTOR,
    unmappedTargetPolicy = ReportingPolicy.ERROR
)
public interface AppjamtampResponseMapper {

    AppjamtampResponse.TeamMissionResponses of(MissionInfo.TeamMissionInfos missionList);

    // TeamMissionInfo to TeamMissionResponse
    @Mapping(source = "completed", target = "isCompleted")
    AppjamtampResponse.TeamMissionResponse toResponse(MissionInfo.TeamMissionInfo info);
}
