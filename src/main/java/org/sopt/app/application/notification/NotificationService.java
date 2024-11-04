package org.sopt.app.application.notification;

import java.util.List;
import java.util.Objects;

import lombok.RequiredArgsConstructor;
import lombok.val;
import org.sopt.app.common.exception.BadRequestException;
import org.sopt.app.common.response.ErrorCode;
import org.sopt.app.domain.entity.Notification;
import org.sopt.app.domain.entity.User;
import org.sopt.app.domain.enums.NotificationType;
import org.sopt.app.interfaces.postgres.NotificationRepository;
import org.sopt.app.interfaces.postgres.UserRepository;
import org.sopt.app.presentation.notification.NotificationRequest.RegisterNotificationRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class NotificationService {

    private final NotificationRepository notificationRepository;
    private final UserRepository userRepository;

    @Transactional(readOnly = true)
    public Notification findNotification(User user, String notificationId) {
        return notificationRepository.findByNotificationIdAndUserId(notificationId, user.getId())
                .orElseThrow(() -> new BadRequestException(ErrorCode.NOTIFICATION_NOT_FOUND));
    }

    @Transactional(readOnly = true)
    public List<Notification> findNotificationList(User user, Pageable pageable) {
        return notificationRepository.findAllByUserId(user.getId(), pageable);
    }

    @Transactional
    public void registerNotification(RegisterNotificationRequest registerNotificationRequest) {
        if (registerNotificationRequest.getType().equals(NotificationType.SEND_ALL)) {
            registerTo(userRepository.findAllPlaygroundId(), registerNotificationRequest);
            return;
        }

        if (registerNotificationRequest.getType().equals(NotificationType.SEND)) {
            registerTo(registerNotificationRequest.getPlaygroundIds().stream().map(Long::parseLong).toList(),
                    registerNotificationRequest);
        }
    }
    private void registerTo(List<Long> playgroundIds, RegisterNotificationRequest request) {
        val targetUserIds = userRepository.findAllIdByPlaygroundIdIn(playgroundIds);
        val notifications = targetUserIds.stream().map(userId -> request.toEntity(request, userId)).toList();
        notificationRepository.saveAll(notifications);
    }

    @Transactional
    public void updateNotificationIsRead(User user, String notificationId) {
        if (Objects.isNull(notificationId)) {
            updateAllNotificationIsRead(user);
        } else {
            updateSingleNotificationIsRead(user, notificationId);
        }
    }

    private void updateSingleNotificationIsRead(User user, String notificationId) {
        val notification = notificationRepository.findByNotificationIdAndUserId(notificationId, user.getId())
                .orElseThrow(() -> new BadRequestException(ErrorCode.NOTIFICATION_NOT_FOUND));
        notification.updateIsRead();
        notificationRepository.save(notification);
    }

    private void updateAllNotificationIsRead(User user) {
        val notificationList = notificationRepository.findAllByUserId(user.getId());
        val readNotificationList = notificationList.stream().peek(Notification::updateIsRead).toList();
        notificationRepository.saveAll(readNotificationList);
    }

    @Transactional(readOnly = true)
    public boolean getNotificationConfirmStatus(Long userId) {
        val notificationList = notificationRepository.findAllByUserId(userId);
        val unreadNotificationList = notificationList.stream()
                .filter(notification -> !notification.getIsRead())
                .toList();
        return unreadNotificationList.isEmpty();
    }
}
