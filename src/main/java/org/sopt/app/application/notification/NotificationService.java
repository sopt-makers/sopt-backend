package org.sopt.app.application.notification;

import lombok.RequiredArgsConstructor;
import org.sopt.app.interfaces.postgres.NotificationRepository;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class NotificationService {

    private final NotificationRepository notificationRepository;
}
