package org.sopt.app.presentation.rank;

import java.util.List;
import org.mapstruct.InjectionStrategy;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;
import org.sopt.app.application.rank.RankInfo.Main;
import org.sopt.app.domain.entity.Mission;
import org.sopt.app.domain.entity.User;

@Mapper(
        componentModel = "spring",
        injectionStrategy = InjectionStrategy.CONSTRUCTOR,
        unmappedTargetPolicy = ReportingPolicy.ERROR
)
public interface RankResponseMapper {

    List<RankResponse.RankMain> of(List<Main> rank);

    RankResponse.Detail of(User user, List<Mission> userMissions);
}
