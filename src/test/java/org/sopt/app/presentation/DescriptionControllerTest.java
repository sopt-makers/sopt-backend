package org.sopt.app.presentation;

import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.transaction.annotation.Transactional;

@Transactional
@AutoConfigureMockMvc
@SpringBootTest
public class DescriptionControllerTest {

    private final String BASE_URI = "/api/v2/description";
    @Autowired
    private MockMvc mockMvc;

    @Test
    @WithUserDetails("1")
    @DisplayName("성공 - 메인 문구 조회")
    void getMainDescriptionSuccess() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get(BASE_URI + "/main"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andDo(print());
    }

    @Test
    @WithUserDetails("1")
    @DisplayName("실패 - 메인 문구 조회 (플그 멤버 프로필)")
    void getMainDescriptionFailGetPlaygroundMemberProfile() {
        Assertions.assertThatThrownBy(() -> mockMvc.perform(MockMvcRequestBuilders.get(BASE_URI + "/main"))
                .andExpect(status().isInternalServerError())
                .andDo(print()));
    }
}
