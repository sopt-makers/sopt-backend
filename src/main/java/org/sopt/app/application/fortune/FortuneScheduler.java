package org.sopt.app.application.fortune;

import java.util.List;
import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import lombok.RequiredArgsConstructor;
import org.sopt.app.application.notification.NotificationService;
import org.sopt.app.common.event.Events;
import org.sopt.app.domain.enums.NotificationCategory;
import org.sopt.app.domain.enums.NotificationType;
import org.sopt.app.presentation.notification.NotificationRequest.RegisterNotificationRequest;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class FortuneScheduler {
    private final NotificationService notificationService;

    @Scheduled(cron = "0 0 9 * * ?")
    public void runDailyFortuneCreation() {
        RegisterNotificationRequest registerNotificationRequest = createFortuneNotificationRequest();
        List<Long> playgroundIds = notificationService.registerNotification(registerNotificationRequest);
        try (ExecutorService executorService = Executors.newVirtualThreadPerTaskExecutor()) {
            playgroundIds.forEach(userId ->
                    executorService.submit(() -> Events.raise(FortuneEvent.of(userId)))
            );
        }
    }

    private RegisterNotificationRequest createFortuneNotificationRequest() {
        return RegisterNotificationRequest.builder()
                .notificationId(UUID.randomUUID().toString())
                .title("오늘의 솝마디")
                .content("오늘의 솝마디를 확인해보세요!")
                .type(NotificationType.SEND_ALL)
                .category(NotificationCategory.NEWS)
                .deepLink("home/fortune")
                .build();
    }
}