package org.sopt.app.application.soptletter;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import java.sql.Connection;
import java.time.Clock;
import java.util.Optional;
import javax.sql.DataSource;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
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
import org.sopt.app.common.utils.AnonymousNameGenerator;
import org.sopt.app.domain.entity.soptletter.SoptLetter;
import org.sopt.app.domain.entity.soptletter.SoptLetterProfile;
import org.sopt.app.interfaces.postgres.soptletter.SoptLetterLikeNotificationRepository;
import org.sopt.app.interfaces.postgres.soptletter.SoptLetterLikeRepository;
import org.sopt.app.interfaces.postgres.soptletter.SoptLetterProfileRepository;
import org.sopt.app.interfaces.postgres.soptletter.SoptLetterRepository;
import org.sopt.app.interfaces.postgres.soptletter.SoptLetterTopicRepository;
import org.springframework.aop.framework.ProxyFactory;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.jdbc.datasource.DataSourceUtils;
import org.springframework.transaction.annotation.AnnotationTransactionAttributeSource;
import org.springframework.transaction.interceptor.TransactionInterceptor;
import org.springframework.transaction.PlatformTransactionManager;
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
        listener.sendLikeAlarm(new SoptLetterLikeEvent(1L, 2L, 3L, 125L));

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
        listener.sendLikeAlarm(new SoptLetterLikeEvent(1L, 2L, 3L, 125L));
        verifyNoInteractions(instantAlarmSender);
    }

    @Test
    @DisplayName("FAIL_알림 서버 장애는 좋아요 처리로 전파하지 않는다")
    void FAIL_sendLikeAlarm_senderFailure() {
        when(platformService.getStatus(2L)).thenReturn(UserStatus.ACTIVE);
        stubNotificationContent();
        doThrow(new IllegalStateException("알림 장애")).when(instantAlarmSender).send(any());
        listener.sendLikeAlarm(new SoptLetterLikeEvent(1L, 2L, 3L, 125L));
        verify(instantAlarmSender).send(any());
    }

    @Test
    @DisplayName("FAIL_활동기수 확인 실패 시 알림을 발송하지 않는다")
    void FAIL_sendLikeAlarm_statusFailure() {
        // given
        when(platformService.getStatus(2L)).thenThrow(new IllegalStateException("인증 서버 장애"));

        // when
        listener.sendLikeAlarm(new SoptLetterLikeEvent(1L, 2L, 3L, 125L));

        // then
        verifyNoInteractions(instantAlarmSender);
    }

    @Test
    @DisplayName("FAIL_문구 설정 조회 실패 시 알림을 보내지 않는다")
    void FAIL_sendLikeAlarm_configFailure() {
        when(platformService.getStatus(2L)).thenReturn(UserStatus.ACTIVE);
        when(operationConfigService.getOperationConfigValue(OperationConfigCategory.SOPT_LETTER, "likeNotificationTitle"))
            .thenThrow(new IllegalStateException("설정 없음"));
        listener.sendLikeAlarm(new SoptLetterLikeEvent(1L, 2L, 3L, 125L));
        verifyNoInteractions(instantAlarmSender);
    }

    @Test
    @DisplayName("FAIL_문구가 공백이면 알림을 보내지 않는다")
    void FAIL_sendLikeAlarm_blankContent() {
        when(platformService.getStatus(2L)).thenReturn(UserStatus.ACTIVE);
        when(operationConfigService.getOperationConfigValue(OperationConfigCategory.SOPT_LETTER, "likeNotificationTitle")).thenReturn(" ");
        when(operationConfigService.getOperationConfigValue(OperationConfigCategory.SOPT_LETTER, "likeNotificationContent")).thenReturn("내용");
        listener.sendLikeAlarm(new SoptLetterLikeEvent(1L, 2L, 3L, 125L));
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

    @ParameterizedTest
    @CsvSource({"false, false", "true, false", "false, true"})
    @DisplayName("SUCCESS_연결 풀이 한 개여도 좋아요와 이력을 같은 트랜잭션으로 커밋하거나 롤백한다")
    void SUCCESS_singleConnectionTransaction(boolean historyFailure, boolean senderFailure) throws Exception {
        Connection connection = mock(Connection.class);
        when(connection.getAutoCommit()).thenReturn(true);
        when(connection.isValid(anyInt())).thenReturn(true);
        DataSource physicalDataSource = mock(DataSource.class);
        when(physicalDataSource.getConnection()).thenReturn(connection);
        HikariConfig config = new HikariConfig();
        config.setDataSource(physicalDataSource);
        config.setMaximumPoolSize(1);
        config.setMinimumIdle(0);
        config.setConnectionTimeout(250);

        try (HikariDataSource pool = new HikariDataSource(config);
             var context = new AnnotationConfigApplicationContext()) {
            context.registerBean(TransactionalEventListenerFactory.class);
            context.registerBean(SoptLetterLikeEventListener.class, () -> listener);
            context.refresh();
            var transactionManager = new DataSourceTransactionManager(pool);
            var letters = mock(SoptLetterRepository.class);
            var profiles = mock(SoptLetterProfileRepository.class);
            var likes = mock(SoptLetterLikeRepository.class);
            var history = mock(SoptLetterLikeNotificationRepository.class);
            var historyProxy = transactionalProxy(history, transactionManager);
            var service = new SoptLetterService(profiles, mock(AnonymousNameGenerator.class), letters,
                mock(SoptLetterTopicRepository.class), likes, historyProxy, mock(SoptLetterGenerator.class),
                operationConfigService, Clock.systemUTC(), context);
            var serviceProxy = transactionalProxy(service, transactionManager);
            when(letters.findById(125L)).thenReturn(Optional.of(
                SoptLetter.builder().id(125L).topicId(3L).authorProfileId(10L).build()));
            when(profiles.findById(10L)).thenReturn(Optional.of(
                SoptLetterProfile.builder().id(10L).userId(2L).build()));
            when(likes.insertIgnore(1L, 125L)).thenReturn(1);
            when(history.insertIgnore(1L, 125L)).thenAnswer(invocation -> {
                assertThat(DataSourceUtils.isConnectionTransactional(DataSourceUtils.getConnection(pool), pool)).isTrue();
                assertThat(pool.getHikariPoolMXBean().getActiveConnections()).isEqualTo(1);
                verifyNoInteractions(instantAlarmSender);
                if (historyFailure) {
                    throw new IllegalStateException("알림 이력 저장 실패");
                }
                return 1;
            });

            if (historyFailure) {
                assertThatThrownBy(() -> serviceProxy.addLike(1L, 3L, 125L))
                    .isInstanceOf(IllegalStateException.class).hasMessage("알림 이력 저장 실패");
                verify(connection).rollback();
                verify(connection, never()).commit();
                verifyNoInteractions(instantAlarmSender, platformService, operationConfigService);
            } else {
                when(platformService.getStatus(2L)).thenReturn(UserStatus.ACTIVE);
                stubNotificationContent();
                doAnswer(invocation -> {
                    verify(connection).commit();
                    if (senderFailure) {
                        throw new IllegalStateException("알림 서버 장애");
                    }
                    return null;
                }).when(instantAlarmSender).send(any());
                serviceProxy.addLike(1L, 3L, 125L);
                verify(connection).commit();
                verify(connection, never()).rollback();
                verify(instantAlarmSender).send(any());
            }
            verify(letters).increaseLikeCount(125L);
            verify(history).insertIgnore(1L, 125L);
            assertThat(pool.getHikariPoolMXBean().getActiveConnections()).isZero();
        }
    }

    @SuppressWarnings("unchecked")
    private <T> T transactionalProxy(T target, PlatformTransactionManager transactionManager) {
        TransactionInterceptor interceptor = new TransactionInterceptor();
        interceptor.setTransactionManager(transactionManager);
        interceptor.setTransactionAttributeSource(new AnnotationTransactionAttributeSource());
        ProxyFactory proxy = new ProxyFactory(target);
        proxy.addAdvice(interceptor);
        return (T) proxy.getProxy();
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
                context.publishEvent(new SoptLetterLikeEvent(1L, 2L, 3L, 125L));
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
