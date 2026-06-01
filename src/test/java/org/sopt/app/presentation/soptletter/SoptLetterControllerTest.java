package org.sopt.app.presentation.soptletter;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.sopt.app.application.soptletter.SoptLetterInfo;
import org.sopt.app.common.exception.BadRequestException;
import org.sopt.app.common.exception.ForbiddenException;
import org.sopt.app.common.response.ErrorCode;
import org.sopt.app.common.security.filter.JwtAuthenticationFilter;
import org.sopt.app.common.security.filter.JwtExceptionFilter;
import org.sopt.app.domain.entity.User;
import org.sopt.app.facade.SoptLetterFacade;
import org.sopt.app.presentation.soptletter.dto.SoptLetterResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(SoptLetterController.class)
@AutoConfigureMockMvc(addFilters = false)
class SoptLetterControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private SoptLetterFacade soptLetterFacade;

    @MockBean
    private SoptLetterResponseMapper soptLetterResponseMapper;

    @MockBean
    private JwtAuthenticationFilter jwtAuthenticationFilter;

    @MockBean
    private JwtExceptionFilter jwtExceptionFilter;

    @Test
    @WithMockUser
    @DisplayName("SUCCESS_온보딩 닉네임 생성 및 조회")
    void SUCCESS_getOnboardingNickname() throws Exception {
        String nickname = "테스트닉네임";
        SoptLetterInfo.Nickname info = SoptLetterInfo.Nickname.builder().nickname(nickname).build();
        when(soptLetterFacade.generateProfileNickname(any()))
                .thenReturn(info);
        when(soptLetterResponseMapper.of(any()))
                .thenReturn(new SoptLetterResponse.GeneratedNicknameResponse(nickname));

        mockMvc.perform(get("/api/v2/sopt-letter/onboarding/nickname"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nickname").value(nickname));
    }

    @Test
    @WithMockUser
    @DisplayName("FORBIDDEN_현재 기수가 아닌 경우")
    void FORBIDDEN_getOnboardingNickname() throws Exception {
        when(soptLetterFacade.generateProfileNickname(any()))
                .thenThrow(new ForbiddenException(ErrorCode.FORBIDDEN));

        mockMvc.perform(get("/api/v2/sopt-letter/onboarding/nickname"))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser
    @DisplayName("CONFLICT_이미 온보딩을 완료한 경우")
    void CONFLICT_getOnboardingNickname() throws Exception {
        // ErrorCode.ALREADY_ONBOARDED_SOPT_LETTER is mapped to HttpStatus.CONFLICT (409)
        when(soptLetterFacade.generateProfileNickname(any()))
                .thenThrow(new BadRequestException(ErrorCode.ALREADY_ONBOARDED_SOPT_LETTER));

        mockMvc.perform(get("/api/v2/sopt-letter/onboarding/nickname"))
                .andExpect(status().isConflict());
    }
}
