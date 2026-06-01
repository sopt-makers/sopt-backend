package org.sopt.app.facade;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.sopt.app.application.soptletter.SoptLetterInfo.Nickname;
import org.sopt.app.application.soptletter.SoptLetterService;
import org.sopt.app.common.exception.ConflictException;
import org.sopt.app.common.response.ErrorCode;

@ExtendWith(MockitoExtension.class)
class SoptLetterFacadeTest {

    @Mock
    private SoptLetterService soptLetterService;

    @InjectMocks
    private SoptLetterFacade soptLetterFacade;

    @Test
    @DisplayName("SUCCESS_온보딩되지 않은 유저의 닉네임을 정상적으로 생성한다")
    void SUCCESS_generateProfileNickname() {
        // given
        final Long userId = 1L;
        final String generatedNickname = "익명의 솝트";
        when(soptLetterService.isOnboarded(userId)).thenReturn(false);
        when(soptLetterService.generateNickname()).thenReturn(generatedNickname);

        // when
        Nickname result = soptLetterFacade.generateProfileNickname(userId);

        // then
        assertThat(result.getNickname()).isEqualTo(generatedNickname);
        verify(soptLetterService, times(1)).isOnboarded(userId);
        verify(soptLetterService, times(1)).generateNickname();
    }

    @Test
    @DisplayName("FAIL_이미 온보딩된 유저이면 ConflictException이 발생한다")
    void FAIL_generateProfileNickname_alreadyOnboarded() {
        // given
        final Long userId = 1L;
        when(soptLetterService.isOnboarded(userId)).thenReturn(true);

        // when & then
        assertThatThrownBy(() -> soptLetterFacade.generateProfileNickname(userId))
            .isInstanceOf(ConflictException.class)
            .satisfies(e -> {
                ConflictException exception = (ConflictException) e;
                assertThat(exception.getErrorCode()).isEqualTo(ErrorCode.ALREADY_ONBOARDED_SOPT_LETTER);
            });

        verify(soptLetterService, times(1)).isOnboarded(userId);
        verify(soptLetterService, times(0)).generateNickname();
    }
}
