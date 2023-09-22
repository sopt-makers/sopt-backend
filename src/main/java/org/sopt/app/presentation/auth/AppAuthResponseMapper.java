package org.sopt.app.presentation.auth;

import org.mapstruct.InjectionStrategy;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;
import org.sopt.app.domain.enums.UserStatus;

@Mapper(
        componentModel = "spring",
        injectionStrategy = InjectionStrategy.CONSTRUCTOR,
        unmappedTargetPolicy = ReportingPolicy.ERROR
)
public interface AppAuthResponseMapper {

    AppAuthResponse.Token of(String accessToken, String refreshToken, String playgroundToken, UserStatus status);
}
