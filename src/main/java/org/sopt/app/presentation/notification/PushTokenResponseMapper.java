package org.sopt.app.presentation.notification;

import org.mapstruct.InjectionStrategy;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

@Mapper(
        componentModel = "spring",
        injectionStrategy = InjectionStrategy.CONSTRUCTOR,
        unmappedTargetPolicy = ReportingPolicy.ERROR
)
public interface PushTokenResponseMapper {
    PushTokenResponse.StatusResponse ofStatus(PushTokenResponse.StatusResponse response);

}
