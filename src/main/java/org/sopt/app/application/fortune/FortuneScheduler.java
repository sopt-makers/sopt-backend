package org.sopt.app.application.fortune;

import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.sopt.app.application.notification.NotificationService;
import org.sopt.app.common.event.Events;
import org.sopt.app.domain.enums.NotificationCategory;
import org.sopt.app.domain.enums.NotificationType;
import org.sopt.app.presentation.notification.NotificationRequest.RegisterNotificationRequest;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class FortuneScheduler {
    private final NotificationService notificationService;
    @Value("${app.base.url}")
    private String baseUrl;

    // 매일 아침 9시에 실행되는 스케줄러
    @Scheduled(cron = "0 0 9 * * ?")
    public void runDailyFortuneCreation() {
        RegisterNotificationRequest registerNotificationRequest = createFortuneNotificationRequest();
        List<Long> playgroundIds = notificationService.registerNotification(registerNotificationRequest);
        playgroundIds
                .forEach(userId -> Events.raise(FortuneEvent.of(userId)));
    }

    private RegisterNotificationRequest createFortuneNotificationRequest() {
        return RegisterNotificationRequest.builder()
                .notificationId(UUID.randomUUID().toString())
                .title("오늘의 솝마디")
                .content("오늘의 솝마디를 확인해보세요!")
                .type(NotificationType.SEND_ALL)
                .category(NotificationCategory.NEWS)
                .deepLink(baseUrl + "/api/v2/fortune/word")
                .webLink(baseUrl + "/api/v2/fortune/word")
                .build();
    }
}