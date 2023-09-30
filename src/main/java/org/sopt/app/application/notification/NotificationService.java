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
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class NotificationService {

    private final NotificationRepository notificationRepository;
    private final UserRepository userRepository;

    @Transactional(readOnly = true)
    public List<Notification> findNotificationList(User user, Pageable pageable) {
        return notificationRepository.findAllByPlaygroundId(user.getPlaygroundId(), pageable);
    }

    @Transactional
    public void registerNotification(
            NotificationRequest.RegisterNotificationRequest registerNotificationRequest
    ) {
        List<Long> playgroundIds = new ArrayList<>();
        System.out.println(registerNotificationRequest.toString());
        if (registerNotificationRequest.getType().equals(NotificationType.ALL)) {
            playgroundIds = userRepository.findAllPlaygroundId();
        } else if (registerNotificationRequest.getType().equals(NotificationType.INDIVIDUAL)) {
            playgroundIds = registerNotificationRequest.getUserIds().stream()
                    .map(Long::parseLong)
                    .toList();
        }
        registerTo(playgroundIds, registerNotificationRequest);
    }
    private void registerTo(List<Long> playgroundIds, NotificationRequest.RegisterNotificationRequest registerNotificationRequest) {
        for (Long playgroundId : playgroundIds) {
            val notification = Notification.builder()
                    .playgroundId(playgroundId)
                    .messageId(registerNotificationRequest.getMessageId())
                    .title(registerNotificationRequest.getTitle())
                    .content(registerNotificationRequest.getContent())
                    .type(registerNotificationRequest.getType())
                    .category(registerNotificationRequest.getCategory())
                    .deepLink(registerNotificationRequest.getDeepLink())
                    .webLink(registerNotificationRequest.getWebLink())
                    .isRead(false)
                    .build();
            notificationRepository.save(notification);
        }
    }

    @Transactional
    public void updateNotificationIsRead(User user, Long notificationId) {
        if (Objects.isNull(notificationId)) {
            updateAllNotificationIsRead(user);
        } else {
            updateSingleNotificationIsRead(user, notificationId);
        }
    }

    private void updateAllNotificationIsRead(User user) {
        val notificationList = notificationRepository.findAllByPlaygroundId(user.getId());
        val readNotificationList = notificationList.stream()
                .map(notification -> {
                        notification.updateIsRead();
                        return notification;
                    }
                ).collect(Collectors.toList());
        notificationRepository.saveAll(readNotificationList);
    }

    private void updateSingleNotificationIsRead(User user, Long notificationId) {
        val notification = notificationRepository.findByIdAndPlaygroundId(notificationId, user.getPlaygroundId())
                .orElseThrow(() -> new BadRequestException(ErrorCode.NOTIFICATION_NOT_FOUND.getMessage()));
        notification.updateIsRead();
        notificationRepository.save(notification);
    }

    @Transactional(readOnly = true)
    public Boolean getNotificationMainViewStatus(User user) {
        val notificationList = notificationRepository.findAllByPlaygroundId(user.getPlaygroundId());
        val unreadNotificationList = notificationList.stream()
                .filter(notification -> !notification.getIsRead())
                .toList();
        return unreadNotificationList.size() > 0;
    }
}
