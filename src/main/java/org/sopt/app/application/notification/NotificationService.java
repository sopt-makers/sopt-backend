package org.sopt.app.application.notification;

import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.val;
import org.sopt.app.common.exception.BadRequestException;
import org.sopt.app.common.response.ErrorCode;
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
    public Notification registerNotification(
            Long userId,
            NotificationRequest.RegisterNotificationRequest registerNotificationRequest
    ) {
        val notification = Notification.builder()
                .userId(userId)
                .title(registerNotificationRequest.getTitle())
                .content(registerNotificationRequest.getContent())
                .type(registerNotificationRequest.getType())
                .isRead(false)
                .build();
        return notificationRepository.save(notification);
    }

    @Transactional
    public Notification updateNotificationIsRead(User user, Long notificationId) {
        val notification = notificationRepository.findByIdAndUserId(notificationId, user.getId())
                .orElseThrow(() -> new BadRequestException(ErrorCode.NOTIFICATION_NOT_FOUND.getMessage()));
        notification.updateIsRead();
        return notificationRepository.save(notification);
    }

    @Transactional(readOnly = true)
    public Boolean getNotificationMainViewStatus(User user) {
        val notificationList = notificationRepository.findAllByUserId(user.getId());
        val unreadNotificationList = notificationList.stream()
                .filter(notification -> !notification.getIsRead())
                .collect(Collectors.toList());
        return unreadNotificationList.size() > 0;
    }
}
