package org.sopt.app.presentation.rank;

import java.util.List;
import org.mapstruct.InjectionStrategy;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;
import org.sopt.app.application.soptamp.SoptampPointInfo.Main;
import org.sopt.app.application.soptamp.SoptampUserInfo;
import org.sopt.app.domain.entity.soptamp.Mission;

@Mapper(
        componentModel = "spring",
        injectionStrategy = InjectionStrategy.CONSTRUCTOR,
        unmappedTargetPolicy = ReportingPolicy.ERROR
)
public interface RankResponseMapper {

    List<RankResponse.RankMain> of(List<Main> rank);

    RankResponse.Detail of(SoptampUserInfo user, List<Mission> userMissions);
}
