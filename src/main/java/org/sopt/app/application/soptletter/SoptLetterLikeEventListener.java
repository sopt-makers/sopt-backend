package org.sopt.app.application.soptletter;

import static org.sopt.app.common.config.AsyncConfig.CACHE_SYNC_EXECUTOR;

import java.util.Set;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.sopt.app.application.platform.PlatformService;
import org.sopt.app.application.appservice.OperationConfigService;
import org.sopt.app.common.config.OperationConfigCategory;
import org.sopt.app.common.client.notification.AlarmSender;
import org.sopt.app.common.client.notification.dto.request.InstantAlarmRequest;
import org.sopt.app.domain.enums.NotificationCategory;
import org.sopt.app.domain.enums.UserStatus;
import org.sopt.app.interfaces.postgres.soptletter.SoptLetterLikeNotificationRepository;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Slf4j
@Component
@RequiredArgsConstructor
public class SoptLetterLikeEventListener {

    private static final String LIKE_NOTIFICATION_TITLE_KEY = "likeNotificationTitle";
    private static final String LIKE_NOTIFICATION_CONTENT_KEY = "likeNotificationContent";

    private final PlatformService platformService;
    private final AlarmSender<InstantAlarmRequest> instantAlarmSender;
    private final OperationConfigService operationConfigService;
    private final SoptLetterLikeNotificationRepository soptLetterLikeNotificationRepository;

    @Async(CACHE_SYNC_EXECUTOR)
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void sendLikeAlarm(SoptLetterLikeEvent event) {
        try {
            if (platformService.getStatus(event.authorUserId()) != UserStatus.ACTIVE) {
                return;
            }

            String title = operationConfigService.getOperationConfigValue(OperationConfigCategory.SOPT_LETTER, LIKE_NOTIFICATION_TITLE_KEY);
            String content = operationConfigService.getOperationConfigValue(OperationConfigCategory.SOPT_LETTER, LIKE_NOTIFICATION_CONTENT_KEY);
            if (title.isBlank() || content.isBlank()) {
                throw new IllegalArgumentException("좋아요 알림 제목과 내용은 비어 있을 수 없습니다.");
            }

            InstantAlarmRequest request = InstantAlarmRequest
                .builderWithRequired(Set.of(String.valueOf(event.authorUserId())), title, content, NotificationCategory.NOTICE)
                .deepLink("soptletter?topicId=%d&messageId=%d&pushType=soptletter_liked".formatted(event.topicId(), event.messageId()))
                .build();

            if (soptLetterLikeNotificationRepository.insertIgnore(event.likerUserId(), event.messageId()) == 0) {
                return;
            }

            instantAlarmSender.send(request);
        } catch (Exception e) {
            log.error("솝레터 좋아요 알림 전송 실패: messageId={}", event.messageId(), e);
        }
    }
}
