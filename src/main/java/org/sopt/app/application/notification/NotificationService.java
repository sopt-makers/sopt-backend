package org.sopt.app.application.notification;

import lombok.RequiredArgsConstructor;
import lombok.val;
import org.sopt.app.domain.entity.Notification;
import org.sopt.app.interfaces.postgres.NotificationRepository;
import org.sopt.app.presentation.notification.NotificationRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class NotificationService {

    private final NotificationRepository notificationRepository;

    @Transactional
    public Notification registerNotification(Long userId,
            NotificationRequest.RegisterNotificationRequest registerNotificationRequest) {
        val notification = Notification.builder()
                .userId(userId)
                .title(registerNotificationRequest.getTitle())
                .content(registerNotificationRequest.getContent())
                .build();
        return notificationRepository.save(notification);
    }
}
