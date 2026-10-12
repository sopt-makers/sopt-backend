package org.sopt.app.application.soptletter;

import static org.mockito.Mockito.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.sopt.app.application.platform.PlatformService;
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
class SoptLetterLikeTransactionEventTest {

    @Mock
    private PlatformService platform;

    @Mock
    private AlarmSender<InstantAlarmRequest> sender;

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
            when(platform.getStatus(2L)).thenReturn(UserStatus.ACTIVE);
        }

        try (var context = new AnnotationConfigApplicationContext()) {
            context.registerBean(TransactionalEventListenerFactory.class);
            context.registerBean(SoptLetterLikeEventListener.class, () -> new SoptLetterLikeEventListener(platform, sender));
            context.refresh();

            TransactionTemplate transaction = new TransactionTemplate(new TestTransactionManager());
            transaction.executeWithoutResult(status -> {
                context.publishEvent(new SoptLetterLikeEvent(2L, 3L, 125L));
                verifyNoInteractions(sender);
                if (rollback) {
                    status.setRollbackOnly();
                }
            });

            if (rollback) {
                verifyNoInteractions(sender, platform);
            } else {
                verify(sender).send(any());
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
