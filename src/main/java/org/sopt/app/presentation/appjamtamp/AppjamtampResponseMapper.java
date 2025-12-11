package org.sopt.app.presentation.appjamtamp;

import org.mapstruct.InjectionStrategy;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;
import org.sopt.app.application.mission.MissionInfo.AppjamMissionInfo;
import org.sopt.app.application.mission.MissionInfo.AppjamMissionInfos;
import org.sopt.app.presentation.appjamtamp.AppjamtampResponse.AppjamMissionResponse;
import org.sopt.app.presentation.appjamtamp.AppjamtampResponse.AppjamMissionResponses;

@Mapper(
    componentModel = "spring",
    injectionStrategy = InjectionStrategy.CONSTRUCTOR,
    unmappedTargetPolicy = ReportingPolicy.ERROR
)
public interface AppjamtampResponseMapper {

    AppjamMissionResponses of(AppjamMissionInfos missionList);

    // TeamMissionInfo to TeamMissionResponse
    @Mapping(source = "completed", target = "isCompleted")
    AppjamMissionResponse toResponse(AppjamMissionInfo info);
}
