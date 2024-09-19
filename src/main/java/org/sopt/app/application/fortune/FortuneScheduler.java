package org.sopt.app.application.fortune;

import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.sopt.app.application.notification.NotificationService;
import org.sopt.app.domain.enums.NotificationCategory;
import org.sopt.app.domain.enums.NotificationType;
import org.sopt.app.presentation.notification.NotificationRequest;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class FortuneScheduler {
    private final NotificationService notificationService;
    // 매일 아침 9시에 실행되는 스케줄러
    @Scheduled(cron = "0 0 9 * * ?")
    public void runDailyFortuneCreation() {
        NotificationRequest.RegisterNotificationRequest registerNotificationRequest = createFortuneNotificationRequest();
        notificationService.registerNotification(registerNotificationRequest);
    }

    private NotificationRequest.RegisterNotificationRequest createFortuneNotificationRequest() {
        return NotificationRequest.RegisterNotificationRequest.builder()
                .notificationId(UUID.randomUUID().toString())  // 고유한 알림 ID
                .title("오늘의 솝마디")  // 알림 제목
                .content("오늘의 솝마디를 확인해보세요!")  // 알림 내용
                .type(NotificationType.SEND_ALL)  // 모든 사용자에게 보낼 경우
                .category(NotificationCategory.NEWS)  // 알림 카테고리
                .deepLink("https://app.dev.sopt.org/api/v2/fortune/word")  // Fortune API URL
                .webLink("https://app.dev.sopt.org/api/v2/fortune/word")  // 웹 링크
                .build();
    }
}