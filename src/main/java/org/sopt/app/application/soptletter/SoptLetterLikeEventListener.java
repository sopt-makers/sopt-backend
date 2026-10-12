package org.sopt.app.application.soptletter;

import static org.sopt.app.common.config.AsyncConfig.CACHE_SYNC_EXECUTOR;

import java.util.Set;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.sopt.app.application.platform.PlatformService;
import org.sopt.app.common.client.notification.AlarmSender;
import org.sopt.app.common.client.notification.dto.request.InstantAlarmRequest;
import org.sopt.app.domain.enums.NotificationCategory;
import org.sopt.app.domain.enums.UserStatus;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Slf4j
@Component
@RequiredArgsConstructor
public class SoptLetterLikeEventListener {

    private final PlatformService platformService;
    private final AlarmSender<InstantAlarmRequest> instantAlarmSender;

    @Async(CACHE_SYNC_EXECUTOR)
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void sendLikeAlarm(SoptLetterLikeEvent event) {
        try {
            if (platformService.getStatus(event.authorUserId()) != UserStatus.ACTIVE) {
                return;
            }

            InstantAlarmRequest request = InstantAlarmRequest
                .builderWithRequired(Set.of(String.valueOf(event.authorUserId())), "솝레터에 좋아요가 달렸어요", "내가 작성한 솝레터를 확인해보세요!", NotificationCategory.NOTICE)
                .deepLink("soptletter?topicId=%d&messageId=%d&pushType=soptletter_liked".formatted(event.topicId(), event.messageId()))
                .build();

            instantAlarmSender.send(request);
        } catch (Exception e) {
            log.error("솝레터 좋아요 알림 전송 실패: messageId={}", event.messageId(), e);
        }
    }
}
