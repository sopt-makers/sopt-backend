package org.sopt.app.presentation.soptletter;

import org.mapstruct.InjectionStrategy;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;
import org.sopt.app.application.soptletter.SoptLetterInfo;
import org.sopt.app.presentation.soptletter.dto.SoptLetterResponse.OnboardingProfileResponse;

@Mapper(
    componentModel = "spring",
    injectionStrategy = InjectionStrategy.CONSTRUCTOR,
    unmappedTargetPolicy = ReportingPolicy.ERROR
)
public interface SoptLetterResponseMapper {

    @Mapping(source = "onboarded", target = "isOnboarded")
    OnboardingProfileResponse of(SoptLetterInfo.Profile info);
}
