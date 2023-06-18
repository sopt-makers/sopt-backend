package org.sopt.app.presentation.notification;

import java.util.List;
import org.mapstruct.InjectionStrategy;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;
import org.sopt.app.domain.entity.Notification;
import org.sopt.app.presentation.notification.NotificationResponse.NotificationMain;

@Mapper(
        componentModel = "spring",
        injectionStrategy = InjectionStrategy.CONSTRUCTOR,
        unmappedTargetPolicy = ReportingPolicy.ERROR
)
public interface NotificationResponseMapper {

    NotificationResponse.NotificationMain of(Notification notification);

    List<NotificationMain> ofList(List<Notification> notificationList);

    NotificationResponse.NotificationIsRead ofIsRead(Notification notification);
}
