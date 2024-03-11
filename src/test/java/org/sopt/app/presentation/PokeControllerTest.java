package org.sopt.app.presentation;

import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.google.gson.Gson;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.sopt.app.domain.entity.User;
import org.sopt.app.presentation.poke.PokeResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.transaction.annotation.Transactional;

@Transactional
@AutoConfigureMockMvc
@SpringBootTest
public class PokeControllerTest {

    private final String BASE_URI = "/api/v2/poke";
    @Autowired
    private MockMvc mockMvc;

    @Test
    @DisplayName("200 - 신규 유저인지 조회")
    void getPokeListSuccess() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders
                        .get(BASE_URI + "/new")
                        .with(SecurityMockMvcRequestPostProcessors.user(new User("주어랑", "주어랑1267", 26L))))
                .andExpect(status().isOk())
                .andExpect(content().string(new Gson().toJson(PokeResponse.IsNew.of(true))))
                .andDo(print());
    }
}
