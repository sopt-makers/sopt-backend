package org.sopt.app.presentation.rank;

import java.util.List;
import org.mapstruct.InjectionStrategy;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;
import org.sopt.app.application.rank.RankInfo;
import org.sopt.app.application.rank.RankInfo.Main;

@Mapper(
        componentModel = "spring",
        injectionStrategy = InjectionStrategy.CONSTRUCTOR,
        unmappedTargetPolicy = ReportingPolicy.ERROR
)
public interface RankResponseMapper {

    List<RankResponse.Main> of(List<Main> rank);

    RankResponse.Detail of(RankInfo.Detail rank);

    RankResponse.Profile of(RankInfo.Profile profile);

}
