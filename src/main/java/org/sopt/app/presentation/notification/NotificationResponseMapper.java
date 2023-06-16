package org.sopt.app.presentation.notification;

import org.mapstruct.InjectionStrategy;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;
import org.sopt.app.domain.entity.Notification;

@Mapper(
        componentModel = "spring",
        injectionStrategy = InjectionStrategy.CONSTRUCTOR,
        unmappedTargetPolicy = ReportingPolicy.ERROR
)
public interface NotificationResponseMapper {

    NotificationResponse.Main of(Notification notification);

}
