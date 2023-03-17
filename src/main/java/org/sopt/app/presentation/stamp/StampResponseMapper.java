package org.sopt.app.presentation.stamp;

import org.mapstruct.InjectionStrategy;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;
import org.sopt.app.domain.entity.Stamp;

@Mapper(
        componentModel = "spring",
        injectionStrategy = InjectionStrategy.CONSTRUCTOR,
        unmappedTargetPolicy = ReportingPolicy.ERROR
)
public interface StampResponseMapper {

    StampResponse.Main of(Stamp user);

    StampResponse.Id of(Long stampId);

}
