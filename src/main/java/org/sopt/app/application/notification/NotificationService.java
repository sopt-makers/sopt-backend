package org.sopt.app.application.notification;

import java.util.List;
import java.util.Objects;

import lombok.RequiredArgsConstructor;
import lombok.val;
import org.sopt.app.application.user.UserWithdrawEvent;
import org.sopt.app.common.exception.BadRequestException;
import org.sopt.app.common.response.ErrorCode;
import org.sopt.app.domain.entity.Notification;
import org.sopt.app.domain.entity.User;
import org.sopt.app.domain.enums.NotificationCategory;
import org.sopt.app.domain.enums.NotificationType;
import org.sopt.app.interfaces.postgres.NotificationRepository;
import org.sopt.app.interfaces.postgres.UserRepository;
import org.sopt.app.presentation.notification.NotificationRequest.RegisterNotificationRequest;
import org.springframework.context.event.EventListener;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class NotificationService {

    private final NotificationRepository notificationRepository;
    private final UserRepository userRepository;

    @Transactional(readOnly = true)
    public Notification findNotification(Long userId, String notificationId) {
        return notificationRepository.findByNotificationIdAndUserId(notificationId, userId)
                .orElseThrow(() -> new BadRequestException(ErrorCode.NOTIFICATION_NOT_FOUND));
    }

    @Transactional(readOnly = true)
    public List<Notification> findNotificationList(Long userId, Pageable pageable) {
        return notificationRepository.findAllByUserId(userId, pageable);
    }

    @Transactional(readOnly = true)
    public List<Notification> findNotificationListByCategory(
            Long userId, Pageable pageable, NotificationCategory category
    ) {
        return notificationRepository.findAllByUserIdAndCategory(userId, pageable, category);
    }

    @Transactional
    public void registerNotification(RegisterNotificationRequest registerNotificationRequest) {
        if (registerNotificationRequest.getType().equals(NotificationType.SEND_ALL)) {
            registerTo(userRepository.findAll().stream().map(User::getId).toList(), registerNotificationRequest);
            return;
        }

        if (registerNotificationRequest.getType().equals(NotificationType.SEND)) {
            registerTo(registerNotificationRequest.getUserIds().stream().map(Long::parseLong).toList(),
                    registerNotificationRequest);
        }
    }

    private void registerTo(List<Long> userIds, RegisterNotificationRequest request) {
        val notifications = userIds.stream().map(userId -> request.toEntity(request, userId)).toList();
        notificationRepository.saveAll(notifications);
    }

    @Transactional
    public void updateNotificationIsRead(Long userId, String notificationId) {
        if (Objects.isNull(notificationId)) {
            updateAllNotificationIsRead(userId);
        } else {
            updateSingleNotificationIsRead(userId, notificationId);
        }
    }

    private void updateSingleNotificationIsRead(Long userId, String notificationId) {
        val notification = notificationRepository.findByNotificationIdAndUserId(notificationId, userId)
                .orElseThrow(() -> new BadRequestException(ErrorCode.NOTIFICATION_NOT_FOUND));
        notification.updateIsRead();
        notificationRepository.save(notification);
    }

    private void updateAllNotificationIsRead(Long userId) {
        val notificationList = notificationRepository.findAllByUserId(userId);
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

    @EventListener(UserWithdrawEvent.class)
    public void handleUserWithdrawEvent(final UserWithdrawEvent event) {
        notificationRepository.deleteByUserIdInQuery(event.getUserId());
    }
}
