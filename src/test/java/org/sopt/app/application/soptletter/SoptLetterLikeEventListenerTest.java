package org.sopt.app.application.soptletter;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.sopt.app.application.platform.PlatformService;
import org.sopt.app.application.appservice.OperationConfigService;
import org.sopt.app.common.config.OperationConfigCategory;
import org.sopt.app.common.client.notification.AlarmSender;
import org.sopt.app.common.client.notification.dto.request.InstantAlarmRequest;
import org.sopt.app.domain.enums.UserStatus;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.event.TransactionalEventListenerFactory;
import org.springframework.transaction.support.AbstractPlatformTransactionManager;
import org.springframework.transaction.support.DefaultTransactionStatus;
import org.springframework.transaction.support.TransactionTemplate;

@ExtendWith(MockitoExtension.class)
class SoptLetterLikeEventListenerTest {

    @Mock
    private PlatformService platformService;

    @Mock
    private AlarmSender<InstantAlarmRequest> instantAlarmSender;

    @Mock
    private OperationConfigService operationConfigService;

    @InjectMocks
    private SoptLetterLikeEventListener listener;

    @Test
    @DisplayName("SUCCESS_현재 활동기수 작성자에게 좋아요 알림을 보낸다")
    void SUCCESS_sendLikeAlarm() {
        // given
        when(platformService.getStatus(2L)).thenReturn(UserStatus.ACTIVE);
        stubNotificationContent();

        // when
        listener.sendLikeAlarm(new SoptLetterLikeEvent(2L, 3L, 125L));

        // then
        ArgumentCaptor<InstantAlarmRequest> captor = ArgumentCaptor.forClass(InstantAlarmRequest.class);
        verify(instantAlarmSender).send(captor.capture());
        assertThat(captor.getValue().getUserIds()).containsExactly("2");
        assertThat(captor.getValue().getTitle()).isEqualTo("DB 알림 제목");
        assertThat(captor.getValue().getContent()).isEqualTo("DB 알림 내용");
        assertThat(captor.getValue().getCategory()).isEqualTo("NOTICE");
        assertThat(captor.getValue().getDeepLink()).isEqualTo("soptletter?topicId=3&messageId=125&pushType=soptletter_liked");
    }

    @Test
    @DisplayName("SUCCESS_현재 활동기수가 아닌 작성자에게는 알림을 보내지 않는다")
    void SUCCESS_sendLikeAlarm_inactiveAuthor() {
        when(platformService.getStatus(2L)).thenReturn(UserStatus.INACTIVE);
        listener.sendLikeAlarm(new SoptLetterLikeEvent(2L, 3L, 125L));
        verifyNoInteractions(instantAlarmSender);
    }

    @Test
    @DisplayName("FAIL_알림 서버 장애는 좋아요 처리로 전파하지 않는다")
    void FAIL_sendLikeAlarm_senderFailure() {
        when(platformService.getStatus(2L)).thenReturn(UserStatus.ACTIVE);
        stubNotificationContent();
        doThrow(new IllegalStateException("알림 장애")).when(instantAlarmSender).send(any());
        listener.sendLikeAlarm(new SoptLetterLikeEvent(2L, 3L, 125L));
        verify(instantAlarmSender).send(any());
    }

    @Test
    @DisplayName("FAIL_활동기수 확인 실패 시 알림을 발송하지 않는다")
    void FAIL_sendLikeAlarm_statusFailure() {
        // given
        when(platformService.getStatus(2L)).thenThrow(new IllegalStateException("인증 서버 장애"));

        // when
        listener.sendLikeAlarm(new SoptLetterLikeEvent(2L, 3L, 125L));

        // then
        verifyNoInteractions(instantAlarmSender);
    }

    @Test
    @DisplayName("FAIL_문구 설정 조회 실패 시 알림을 보내지 않는다")
    void FAIL_sendLikeAlarm_configFailure() {
        when(platformService.getStatus(2L)).thenReturn(UserStatus.ACTIVE);
        when(operationConfigService.getOperationConfigValue(OperationConfigCategory.SOPT_LETTER, "likeNotificationTitle"))
            .thenThrow(new IllegalStateException("설정 없음"));
        listener.sendLikeAlarm(new SoptLetterLikeEvent(2L, 3L, 125L));
        verifyNoInteractions(instantAlarmSender);
    }

    @Test
    @DisplayName("FAIL_문구가 공백이면 알림을 보내지 않는다")
    void FAIL_sendLikeAlarm_blankContent() {
        when(platformService.getStatus(2L)).thenReturn(UserStatus.ACTIVE);
        when(operationConfigService.getOperationConfigValue(OperationConfigCategory.SOPT_LETTER, "likeNotificationTitle")).thenReturn(" ");
        when(operationConfigService.getOperationConfigValue(OperationConfigCategory.SOPT_LETTER, "likeNotificationContent")).thenReturn("내용");
        listener.sendLikeAlarm(new SoptLetterLikeEvent(2L, 3L, 125L));
        verifyNoInteractions(instantAlarmSender);
    }

    private void stubNotificationContent() {
        when(operationConfigService.getOperationConfigValue(OperationConfigCategory.SOPT_LETTER, "likeNotificationTitle")).thenReturn("DB 알림 제목");
        when(operationConfigService.getOperationConfigValue(OperationConfigCategory.SOPT_LETTER, "likeNotificationContent")).thenReturn("DB 알림 내용");
    }

    @Test
    @DisplayName("SUCCESS_좋아요 알림은 트랜잭션 커밋 후에만 발송한다")
    void SUCCESS_sendAfterCommit() {
        verifyTransaction(false);
    }

    @Test
    @DisplayName("SUCCESS_좋아요 트랜잭션이 롤백되면 알림을 발송하지 않는다")
    void SUCCESS_skipAfterRollback() {
        verifyTransaction(true);
    }

    private void verifyTransaction(boolean rollback) {
        if (!rollback) {
            when(platformService.getStatus(2L)).thenReturn(UserStatus.ACTIVE);
            stubNotificationContent();
        }

        try (var context = new AnnotationConfigApplicationContext()) {
            context.registerBean(TransactionalEventListenerFactory.class);
            context.registerBean(SoptLetterLikeEventListener.class, () -> new SoptLetterLikeEventListener(platformService, instantAlarmSender, operationConfigService));
            context.refresh();

            TransactionTemplate transaction = new TransactionTemplate(new TestTransactionManager());
            transaction.executeWithoutResult(status -> {
                context.publishEvent(new SoptLetterLikeEvent(2L, 3L, 125L));
                verifyNoInteractions(instantAlarmSender);
                if (rollback) {
                    status.setRollbackOnly();
                }
            });

            if (rollback) {
                verifyNoInteractions(instantAlarmSender, platformService, operationConfigService);
            } else {
                verify(instantAlarmSender).send(any());
            }
        }
    }

    private static class TestTransactionManager extends AbstractPlatformTransactionManager {
        @Override
        protected Object doGetTransaction() {
            return new Object();
        }

        @Override
        protected void doBegin(Object transaction, TransactionDefinition definition) {
        }

        @Override
        protected void doCommit(DefaultTransactionStatus status) {
        }

        @Override
        protected void doRollback(DefaultTransactionStatus status) {
        }
    }
}
