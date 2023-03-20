package org.sopt.app.presentation.rank;

import java.util.List;
import org.mapstruct.InjectionStrategy;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;
import org.mapstruct.ReportingPolicy;
import org.sopt.app.application.rank.RankInfo;
import org.sopt.app.application.rank.RankInfo.Main;
import org.sopt.app.domain.entity.Mission;
import org.sopt.app.domain.entity.User;

@Mapper(
        componentModel = "spring",
        injectionStrategy = InjectionStrategy.CONSTRUCTOR,
        unmappedTargetPolicy = ReportingPolicy.ERROR
)
public interface RankResponseMapper {

    List<RankResponse.Main> of(List<Main> rank);

    @Mappings({@Mapping(source = "user.id", target = "userId")})
    RankResponse.Detail of(User user, List<Mission> userMissions);

    RankResponse.Profile of(RankInfo.Profile profile);

}
