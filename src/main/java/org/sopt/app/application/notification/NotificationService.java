package org.sopt.app.application.notification;

import java.time.LocalDateTime;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.val;
import org.sopt.app.domain.entity.Notification;
import org.sopt.app.domain.entity.User;
import org.sopt.app.interfaces.postgres.NotificationRepository;
import org.sopt.app.presentation.notification.NotificationRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class NotificationService {

    private final NotificationRepository notificationRepository;

    @Transactional(readOnly = true)
    public List<Notification> findNotificationList(User user, Pageable pageable) {
        val notificationList = notificationRepository.findAllByUserId(user.getId(), pageable);
        return notificationList;
    }

    @Transactional
    public Notification registerNotification(Long userId,
            NotificationRequest.RegisterNotificationRequest registerNotificationRequest) {
        val notification = Notification.builder()
                .userId(userId)
                .title(registerNotificationRequest.getTitle())
                .content(registerNotificationRequest.getContent())
                .createdAt(LocalDateTime.now())
                .build();
        return notificationRepository.save(notification);
    }
}
