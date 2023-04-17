package org.sopt.app.presentation.mission;

import java.util.List;
import org.mapstruct.InjectionStrategy;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;
import org.sopt.app.application.mission.MissionInfo;
import org.sopt.app.domain.entity.Mission;

@Mapper(
        componentModel = "spring",
        injectionStrategy = InjectionStrategy.CONSTRUCTOR,
        unmappedTargetPolicy = ReportingPolicy.ERROR
)
public interface MissionResponseMapper {

    MissionResponse.MissionId of(Long missionId);

    List<MissionResponse.MissionMain> of(List<Mission> missionList);

    List<MissionResponse.Completeness> ofCompleteness(List<MissionInfo.Completeness> missionList);

}
