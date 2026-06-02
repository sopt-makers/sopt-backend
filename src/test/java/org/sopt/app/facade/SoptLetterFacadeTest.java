package org.sopt.app.facade;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.sopt.app.application.soptletter.SoptLetterInfo.Profile;
import org.sopt.app.application.soptletter.SoptLetterService;

@ExtendWith(MockitoExtension.class)
class SoptLetterFacadeTest {

    @Mock
    private SoptLetterService soptLetterService;

    @InjectMocks
    private SoptLetterFacade soptLetterFacade;

    @Test
    @DisplayName("SUCCESS_온보딩 프로필 닉네임을 정상적으로 생성하거나 조회한다")
    void SUCCESS_generateProfileNickname() {
        // given
        final Long userId = 1L;
        final String generatedNickname = "익명의 솝트";
        Profile expected = Profile.of(generatedNickname, false);
        when(soptLetterService.getOrCreateProfile(userId)).thenReturn(expected);

        // when
        Profile result = soptLetterFacade.generateProfileNickname(userId);

        // then
        assertThat(result.getNickname()).isEqualTo(generatedNickname);
        assertThat(result.isOnboarded()).isFalse();
        verify(soptLetterService, times(1)).getOrCreateProfile(userId);
    }
}
