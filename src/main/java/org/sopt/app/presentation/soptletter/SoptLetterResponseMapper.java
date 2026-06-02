package org.sopt.app.presentation.soptletter;

import org.mapstruct.InjectionStrategy;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;
import org.sopt.app.application.soptletter.SoptLetterInfo.Profile;
import org.sopt.app.presentation.soptletter.dto.SoptLetterResponse.GeneratedNicknameResponse;

@Mapper(
    componentModel = "spring",
    injectionStrategy = InjectionStrategy.CONSTRUCTOR,
    unmappedTargetPolicy = ReportingPolicy.ERROR
)
public interface SoptLetterResponseMapper {

    @Mapping(source = "onboarded", target = "isOnboarded")
    GeneratedNicknameResponse of(Profile info);
}
