package org.sopt.app.presentation.notification;

import org.mapstruct.InjectionStrategy;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;
import org.sopt.app.domain.entity.UserNotificationOption;

@Mapper(
        componentModel = "spring",
        injectionStrategy = InjectionStrategy.CONSTRUCTOR,
        unmappedTargetPolicy = ReportingPolicy.ERROR
)
public interface OptionResponseMapper {
    OptionResponse.OptIn ofOptIn(UserNotificationOption notificationOption);
}
