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
import org.sopt.app.common.client.notification.AlarmSender;
import org.sopt.app.common.client.notification.dto.request.InstantAlarmRequest;
import org.sopt.app.domain.enums.UserStatus;

@ExtendWith(MockitoExtension.class)
class SoptLetterLikeEventListenerTest {

    @Mock
    private PlatformService platformService;

    @Mock
    private AlarmSender<InstantAlarmRequest> instantAlarmSender;

    @InjectMocks
    private SoptLetterLikeEventListener listener;

    @Test
    @DisplayName("SUCCESS_현재 활동기수 작성자에게 좋아요 알림을 보낸다")
    void SUCCESS_sendLikeAlarm() {
        // given
        when(platformService.getStatus(2L)).thenReturn(UserStatus.ACTIVE);

        // when
        listener.sendLikeAlarm(new SoptLetterLikeEvent(2L, 3L, 125L));

        // then
        ArgumentCaptor<InstantAlarmRequest> captor = ArgumentCaptor.forClass(InstantAlarmRequest.class);
        verify(instantAlarmSender).send(captor.capture());
        assertThat(captor.getValue().getUserIds()).containsExactly("2");
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
}
