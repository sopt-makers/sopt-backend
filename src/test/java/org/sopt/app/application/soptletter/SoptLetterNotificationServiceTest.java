package org.sopt.app.application.soptletter;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.verifyNoInteractions;

import java.util.List;
import java.util.Set;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.sopt.app.application.platform.dto.PlatformUserInfoResponse;
import org.sopt.app.application.soptamp.AuthUserProfileReader;
import org.sopt.app.common.client.notification.AlarmSender;
import org.sopt.app.common.client.notification.dto.request.InstantAlarmRequest;
import org.sopt.app.interfaces.postgres.soptletter.SoptLetterProfileRepository;

@ExtendWith(MockitoExtension.class)
class SoptLetterNotificationServiceTest {

    private static final Long CURRENT_GENERATION = 38L;

    @Mock
    private AuthUserProfileReader authUserProfileReader;

    @Mock
    private SoptLetterProfileRepository soptLetterProfileRepository;

    @Mock
    private AlarmSender<InstantAlarmRequest> instantAlarmSender;

    @Test
    @DisplayName("SUCCESS_현재 활동기수 중 솝레터 미작성 유저에게 리마인드 알림을 전송한다")
    void SUCCESS_sendUnwrittenReminder() {
        // given
        SoptLetterNotificationService service = new SoptLetterNotificationService(
            authUserProfileReader,
            soptLetterProfileRepository,
            instantAlarmSender,
            CURRENT_GENERATION
        );
        Set<Long> currentGenerationUserIds = Set.of(1L, 2L, 3L);
        when(authUserProfileReader.getSoptUserProfilesByGeneration(CURRENT_GENERATION))
            .thenReturn(List.of(
                createActiveUser(1),
                createActiveUser(2),
                createActiveUser(3)
            ));
        when(soptLetterProfileRepository.findWrittenUserIdsByUserIdIn(eq(currentGenerationUserIds)))
            .thenReturn(Set.of(2L));

        // when
        service.sendUnwrittenReminder("솝레터를 작성해보세요", "아직 작성하지 않은 솝레터가 있어요", "sopt://sopt-letter", null);

        // then
        ArgumentCaptor<InstantAlarmRequest> requestCaptor = ArgumentCaptor.forClass(InstantAlarmRequest.class);
        verify(instantAlarmSender).send(requestCaptor.capture());

        InstantAlarmRequest request = requestCaptor.getValue();
        assertThat(request.getUserIds()).containsExactlyInAnyOrder("1", "3");
        assertThat(request.getTitle()).isEqualTo("솝레터를 작성해보세요");
        assertThat(request.getContent()).isEqualTo("아직 작성하지 않은 솝레터가 있어요");
        assertThat(request.getDeepLink()).isEqualTo("sopt://sopt-letter");
        assertThat(request.getWebLink()).isNull();
    }

    @Test
    @DisplayName("SUCCESS_현재 활동기수 유저가 없으면 알림을 전송하지 않는다")
    void SUCCESS_sendUnwrittenReminder_emptyTarget() {
        // given
        SoptLetterNotificationService service = new SoptLetterNotificationService(
            authUserProfileReader,
            soptLetterProfileRepository,
            instantAlarmSender,
            CURRENT_GENERATION
        );
        when(authUserProfileReader.getSoptUserProfilesByGeneration(CURRENT_GENERATION)).thenReturn(List.of());

        // when
        service.sendUnwrittenReminder("솝레터를 작성해보세요", "아직 작성하지 않은 솝레터가 있어요", null, null);

        // then
        verify(instantAlarmSender, never()).send(any());
        verify(soptLetterProfileRepository, never()).findWrittenUserIdsByUserIdIn(Set.of());
    }

    private PlatformUserInfoResponse createActiveUser(int userId) {
        return new PlatformUserInfoResponse(
            userId,
            "솝트",
            null,
            null,
            null,
            null,
            CURRENT_GENERATION.intValue(),
            List.of(new PlatformUserInfoResponse.SoptActivities(1, CURRENT_GENERATION.intValue(), "서버", null, true))
        );
    }

    @Test
    void allUsersHaveWritten() {
        when(authUserProfileReader.getSoptUserProfilesByGeneration(CURRENT_GENERATION)).thenReturn(List.of(createActiveUser(1)));
        when(soptLetterProfileRepository.findWrittenUserIdsByUserIdIn(Set.of(1L))).thenReturn(Set.of(1L));
        service().sendUnwrittenReminder("제목", "내용", null, null);
        verifyNoInteractions(instantAlarmSender);
    }

    @Test
    void profileLookupFailureDoesNotSend() {
        when(authUserProfileReader.getSoptUserProfilesByGeneration(CURRENT_GENERATION)).thenReturn(List.of(createActiveUser(1)));
        when(soptLetterProfileRepository.findWrittenUserIdsByUserIdIn(Set.of(1L))).thenThrow(new IllegalStateException("조회 실패"));
        assertThatThrownBy(() -> service().sendUnwrittenReminder("제목", "내용", null, null)).isInstanceOf(IllegalStateException.class);
        verifyNoInteractions(instantAlarmSender);
    }

    @Test
    void activeUserLookupFailureDoesNotSend() {
        when(authUserProfileReader.getSoptUserProfilesByGeneration(CURRENT_GENERATION)).thenThrow(new IllegalStateException("조회 실패"));
        assertThatThrownBy(() -> service().sendUnwrittenReminder("제목", "내용", null, null)).isInstanceOf(IllegalStateException.class);
        verifyNoInteractions(instantAlarmSender, soptLetterProfileRepository);
    }

    private SoptLetterNotificationService service() {
        return new SoptLetterNotificationService(authUserProfileReader, soptLetterProfileRepository, instantAlarmSender, CURRENT_GENERATION);
    }
}
