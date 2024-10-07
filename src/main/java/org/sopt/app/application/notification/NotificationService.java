package org.sopt.app.application.notification;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import lombok.RequiredArgsConstructor;
import lombok.val;
import org.sopt.app.common.exception.BadRequestException;
import org.sopt.app.common.response.ErrorCode;
import org.sopt.app.domain.entity.Notification;
import org.sopt.app.domain.entity.User;
import org.sopt.app.domain.enums.NotificationType;
import org.sopt.app.interfaces.postgres.NotificationRepository;
import org.sopt.app.interfaces.postgres.UserRepository;
import org.sopt.app.presentation.notification.NotificationRequest;
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
    @Deprecated
    public Notification findNotificationDeprecated(User user, Long notificationId) {
        return notificationRepository.findByIdAndUserId(notificationId, user.getId())
                .orElseThrow(() -> new BadRequestException(ErrorCode.NOTIFICATION_NOT_FOUND));
    }

    @Transactional(readOnly = true)
    public List<Notification> findNotificationList(User user, Pageable pageable) {
        return notificationRepository.findAllByUserId(user.getId(), pageable);
    }

    @Transactional
    public void registerNotification(RegisterNotificationRequest registerNotificationRequest) {
        List<Long> playgroundIds = new ArrayList<>();
        if (registerNotificationRequest.getType().equals(NotificationType.SEND_ALL)) {
            playgroundIds = userRepository.findAllPlaygroundId();
        } else if (registerNotificationRequest.getType().equals(NotificationType.SEND)) {
            playgroundIds = registerNotificationRequest.getPlaygroundIds().stream()
                    .map(Long::parseLong)
                    .toList();
        }
        registerTo(playgroundIds, registerNotificationRequest);
    }
    private void registerTo(List<Long> playgroundIds, NotificationRequest.RegisterNotificationRequest registerNotificationRequest) {
        val targetUserIds = userRepository.findAllIdByPlaygroundIdIn(playgroundIds);
        val notifications = targetUserIds.stream()
                .map(userId -> Notification.builder()
                        .userId(userId)
                        .notificationId(registerNotificationRequest.getNotificationId())
                        .title(registerNotificationRequest.getTitle())
                        .content(registerNotificationRequest.getContent())
                        .type(registerNotificationRequest.getType())
                        .category(registerNotificationRequest.getCategory())
                        .deepLink(registerNotificationRequest.getDeepLink())
                        .webLink(registerNotificationRequest.getWebLink())
                        .isRead(false)
                        .build()
                ).toList();
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
    @Transactional
    @Deprecated
    public void updateNotificationIsReadDeprecated(User user, Long notificationId) {
        if (Objects.isNull(notificationId)) {
            updateAllNotificationIsRead(user);
        } else {
            updateSingleNotificationIsReadDeprecated(user, notificationId);
        }
    }

    private void updateSingleNotificationIsRead(User user, String notificationId) {
        val notification = notificationRepository.findByNotificationIdAndUserId(notificationId, user.getId())
                .orElseThrow(() -> new BadRequestException(ErrorCode.NOTIFICATION_NOT_FOUND));
        notification.updateIsRead();
        notificationRepository.save(notification);
    }

    @Deprecated
    private void updateSingleNotificationIsReadDeprecated(User user, Long notificationId) {
        val notification = notificationRepository.findByIdAndUserId(notificationId, user.getId())
                .orElseThrow(() -> new BadRequestException(ErrorCode.NOTIFICATION_NOT_FOUND));
        notification.updateIsRead();
        notificationRepository.save(notification);
    }

    private void updateAllNotificationIsRead(User user) {
        val notificationList = notificationRepository.findAllByUserId(user.getId());
        val readNotificationList = notificationList.stream()
                .map(notification -> {
                        notification.updateIsRead();
                        return notification;
                    }
                ).collect(Collectors.toList());
        notificationRepository.saveAll(readNotificationList);
    }

    @Transactional(readOnly = true)
    public Boolean getNotificationConfirmStatus(User user) {
        val notificationList = notificationRepository.findAllByUserId(user.getId());
        val unreadNotificationList = notificationList.stream()
                .filter(notification -> !notification.getIsRead())
                .toList();
        return unreadNotificationList.isEmpty();
    }
}
