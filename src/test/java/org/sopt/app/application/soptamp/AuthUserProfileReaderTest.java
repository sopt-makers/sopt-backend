package org.sopt.app.application.soptamp;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowCallbackHandler;
import org.springframework.test.util.ReflectionTestUtils;

@ExtendWith(MockitoExtension.class)
class AuthUserProfileReaderTest {

    @Mock
    JdbcTemplate jdbcTemplate;

    @InjectMocks
    AuthUserProfileReader authUserProfileReader;

    @Test
    @DisplayName("기수별 조회는 해당 기수 SOPT 활동 유저의 전체 활동 이력을 auth 스키마에서 읽는다")
    void getSoptUserProfilesByGeneration() {
        ReflectionTestUtils.setField(authUserProfileReader, "authSchema", "auth");

        authUserProfileReader.getSoptUserProfilesByGeneration(39L);

        ArgumentCaptor<String> sqlCaptor = ArgumentCaptor.forClass(String.class);
        verify(jdbcTemplate).query(sqlCaptor.capture(), any(RowCallbackHandler.class), eq(39L));
        String sql = sqlCaptor.getValue().replaceAll("\\s+", " ").trim();
        assertThat(sql)
            .contains("FROM auth.users u")
            .contains("JOIN auth.user_activity_histories uah ON uah.user_id = u.id")
            .contains("WHERE u.id IN ( SELECT user_id FROM auth.user_activity_histories WHERE generation = ? AND is_sopt = true )")
            .doesNotContain("role =");
    }

    @Test
    @DisplayName("전체 조회는 필터 없이 전체 유저의 활동 이력을 읽는다")
    void getAllUserProfiles() {
        ReflectionTestUtils.setField(authUserProfileReader, "authSchema", "auth");

        authUserProfileReader.getAllUserProfiles();

        ArgumentCaptor<String> sqlCaptor = ArgumentCaptor.forClass(String.class);
        verify(jdbcTemplate).query(sqlCaptor.capture(), any(RowCallbackHandler.class), any(Object[].class));
        String sql = sqlCaptor.getValue().replaceAll("\\s+", " ").trim();
        assertThat(sql)
            .contains("FROM auth.users u")
            .doesNotContain("WHERE");
    }
}
